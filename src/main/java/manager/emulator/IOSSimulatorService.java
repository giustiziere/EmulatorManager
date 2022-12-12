package manager.emulator;

import com.fasterxml.jackson.databind.ObjectMapper;
import manager.dto.Device;
import manager.dto.ResponseDTO;
import manager.dto.ios.IOSDeviceListDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static manager.executor.ShellExecutorService.sh;
import static manager.executor.ShellExecutorService.shOut;
import static manager.utils.Constants.*;

public class IOSSimulatorService implements IEmulator {

    private List<Device> getIOSDeviceListDTOMapper(IOSDeviceListDTO dto) {
        var list = new ArrayList<Device>();
        dto.getDevices().forEach((runtime, simulators) ->
                simulators.forEach(simulator ->
                        list.add(new Device()
                                .setPlatform(IOS)
                                .setName(simulator.getName())
                                .setOsVersion(new StringBuilder(runtime.replaceAll("\\D+", ""))
                                        .insert(2, ".")
                                        .toString())
                                .setId(simulator.getUdid())
                                .setStatus(simulator.getState()))));
        return list;
    }

    @Override
    public ResponseDTO<List<Device>> getDeviceList() {
        var response = new ResponseDTO<List<Device>>();
        try {
            var listString = shOut(LIST_SIMS);
            var iosDeviceListDTO = new ObjectMapper().readValue(listString, IOSDeviceListDTO.class);
            response
                    .setSuccess(true)
                    .setResponse(getIOSDeviceListDTOMapper(iosDeviceListDTO));
        } catch (IOException | InterruptedException e) {
            response
                    .setSuccess(false)
                    .setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseDTO<String> createAndRunEmulator(String deviceNames, String runtimes, Boolean isHeadless) {
        var response = new ResponseDTO<String>();
        var deviceList = Arrays.asList(deviceNames.split(","));
        var runtimeList = new ArrayList<>(Arrays.asList(runtimes.split(","))); //not fixed size
        if (deviceList.size() > 1 && runtimeList.size() == 1) {
            for (int i = 0; i < deviceList.size() - 1; i++) {
                runtimeList.add(runtimeList.get(0));
            }
        } else {
            if (deviceList.size() != runtimeList.size())
                return response
                        .setSuccess(false)
                        .setMessage(INCORRECT_DEVICE_LIST);
        }
        var deviceMap = IntStream.range(0, deviceList.size()).boxed()
                .collect(Collectors.toMap(deviceList::get, runtimeList::get));
        var operationStatus = new AtomicInteger();
        var stb = new StringBuilder();
        deviceMap.forEach((device, runtime) -> {
            try {
                if (!isEmulatorExist(device, runtime)) {
                    var status = sh(String.format(CREATE_SIM_COMMAND, device, device, runtime.replace(".", "-")));
                    operationStatus.addAndGet(status);
                    if (status == 0)
                        stb.append(String.format(DEVICE_ADDED, device, runtime)).append(SEPARATOR);
                    else
                        stb.append(String.format(DEVICE_FAILED, device, runtime, status)).append(SEPARATOR);
                } else {
                    operationStatus.addAndGet(0);
                    stb.append(String.format(DEVICE_ALREADY_EXISTS, device, runtime)).append(SEPARATOR);
                }
                if (!isEmulatorBooted(device, runtime) && getDeviceByNameAndRuntime(device, runtime).isPresent()) {
                    Thread.sleep(2000);
                    var bootStatus = sh(String.format(BOOT_SIM_COMMAND, getDeviceByNameAndRuntime(device, runtime).get().getId()));
                    operationStatus.addAndGet(bootStatus);
                    stb.append(String.format(DEVICE_BOOTED, device, runtime)).append(SEPARATOR);
                } else {
                    operationStatus.addAndGet(1);
                    stb.append(String.format(DEVICE_NOT_BOOTED, device, runtime)).append(SEPARATOR);
                }
            } catch (IOException | InterruptedException e) {
                stb.append(e.getMessage()).append(SEPARATOR);
            }
        });
        return response
                .setSuccess(operationStatus.get() == 0)
                .setResponse(stb.toString());
    }

    @Override
    public ResponseDTO<String> deleteAllEmulators() {
        var response = new ResponseDTO<String>();
        try {
            var status = sh(DELETE_SIMS) == 0;
            response
                    .setSuccess(status)
                    .setResponse(status ? DELETION_SUCCESS : DELETION_FAILURE);
        } catch (IOException | InterruptedException e) {
            response
                    .setSuccess(false)
                    .setResponse(DELETION_FAILURE)
                    .setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseDTO<String> addContacts(String deviceName) {
        var response = new ResponseDTO<String>();
        try {
            var status0 = sh(String.format("xcrun simctl addmedia '%s' '%s'",
                    deviceName, "src/test/resources/contacts/goshan.vcf"));
            var status1 = sh(String.format("xcrun simctl addmedia '%s' '%s'",
                    deviceName, "src/test/resources/contacts/vasya.vcf"));
            boolean success = status0 + status1 == 0;
            response
                    .setSuccess(success)
                    .setResponse(success ? ADD_CONTACTS_SUCCESS : ADD_CONTACTS_FAILURE);
        } catch (IOException | InterruptedException e) {
            response
                    .setSuccess(false)
                    .setResponse(ADD_CONTACTS_FAILURE)
                    .setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseDTO<String> deleteApp(String deviceName, String bundleId) {
        var response = new ResponseDTO<String>();
        var devices = getDeviceList();
        var device = devices.getResponse().stream()
                .filter(v -> v.getName().equals(deviceName))
                .findFirst();
        if (device.isEmpty()) {
            return response
                    .setSuccess(false)
                    .setResponse(String.format(DELETE_APP_FAILURE, bundleId));
        }
        try {
            var status = sh(String.format("xcrun simctl uninstall %s %s", device.get().getId(), bundleId));
            response
                    .setSuccess(status == 0)
                    .setResponse(String.format(status == 0 ? DELETE_APP_SUCCESS : DELETE_APP_FAILURE, bundleId));
        } catch (IOException | InterruptedException e) {
            response
                    .setSuccess(false)
                    .setResponse(ADD_CONTACTS_FAILURE)
                    .setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseDTO<String> killWDASession(String deviceName) {
        var response = new ResponseDTO<String>();
        var devices = getDeviceList();
        var udid = devices.getResponse().stream()
                .filter(device -> device.getName().equals(deviceName))
                .findFirst();
        if (udid.isEmpty()) {
            return response.setSuccess(false);
        }
        try {
            var status = sh(String.format("pkill -f \"WebDriverAgent.*%s.*\"", udid.get()));
            response.setSuccess(status == 0);
        } catch (IOException | InterruptedException e) {
            response
                    .setSuccess(false)
                    .setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseDTO<Boolean> isFileExistsAtEmulator(String deviceName, String filePath) {
        var response = new ResponseDTO<Boolean>();
        var devices = getDeviceList();
        var device = devices.getResponse().stream()
                .filter(v -> v.getName().equals(deviceName))
                .findFirst();
        if (device.isEmpty()) {
            return response
                    .setSuccess(false)
                    .setMessage("Device not found!")
                    .setResponse(false);
        }
        try {
            String out = shOut(String.format("find ~/Library/Developer/CoreSimulator/Devices/%s/data -name \"%s\"",
                    device.get().getId(), filePath));
            response
                    .setSuccess(true)
                    .setResponse(out.contains(filePath));
        } catch (IOException | InterruptedException e) {
            response
                    .setSuccess(false)
                    .setResponse(false)
                    .setMessage(e.getMessage());
        }
        return response;
    }
}
