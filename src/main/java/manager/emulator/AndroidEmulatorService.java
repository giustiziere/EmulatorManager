package manager.emulator;

import manager.dto.Device;
import manager.dto.ResponseDTO;
import manager.dto.android.ApiLevel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static manager.executor.ShellExecutorService.*;
import static manager.utils.Constants.*;
import static manager.utils.Utils.regexMatcher;

public class AndroidEmulatorService implements IEmulator {

    private List<Device> getAndroidDeviceListDTOMapper(String avdList) throws IOException, InterruptedException {
        var list = new ArrayList<Device>();
        var names = regexMatcher("\\s+Name:\\s(.+)\n", avdList, 1);
        for (int i = 0; i < names.size(); i++) {
            var device = new Device();
            var osVersionFullString = regexMatcher("\\s+Based on:\\s(.+)\n", avdList, 1).get(i);
            var osVersion = regexMatcher("Android (\\d+.\\d+)", osVersionFullString, 1).get(0);
            if (osVersion.endsWith(".0"))
                osVersion = osVersion.substring(0, osVersion.indexOf("."));
            var runningEmus = shOut(ADB_DEVICES).split("\n");
            String status = "Shutdown";
            for (String runningEmu : runningEmus) {
                if (!runningEmu.isEmpty()) {
                    var emuProp = shOut(String.format(ADB_GET_PROPERTY, runningEmu, EMU_AT_PROPERTY));
                    if (emuProp.equals(names.get(i))) {
                        device.setId(runningEmu);
                        status = "Booted";
                        break;
                    }
                }
            }
            list.add(device.setPlatform(ANDROID)
                    .setName(names.get(i))
                    .setOsVersion(osVersion)
                    .setStatus(status)
            );
        }
        return list;
    }

    @Override
    public ResponseDTO<List<Device>> getDeviceList() {
        var response = new ResponseDTO<List<Device>>();
        try {
            var listString = shOut(LIST_AVD);
            response
                    .setSuccess(true)
                    .setResponse(getAndroidDeviceListDTOMapper(listString));
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
                    var createStatus = sh(String.format(CREATE_EMU_COMMAND, device, ApiLevel.getApiByVersion(runtime).getApiLevel()));
                    Thread.sleep(2000);
                    operationStatus.addAndGet(createStatus);
                    if (createStatus == 0)
                        stb.append(String.format(DEVICE_ADDED, device, runtime)).append(SEPARATOR);
                    else
                        stb.append(String.format(DEVICE_FAILED, device, runtime, createStatus)).append(SEPARATOR);
                } else {
                    operationStatus.addAndGet(0);
                    stb.append(String.format(DEVICE_ALREADY_EXISTS, device, runtime)).append(SEPARATOR);
                }
                if (!isEmulatorBooted(device, runtime)) {
                    sh(String.format(RUN_EMU_COMMAND, device, isHeadless == null || !isHeadless ? "" : "-no-window", EMU_AT_PROPERTY, device));
                    Thread.sleep(2000);
                    stb.append(String.format(RUN_EMULATOR, device, runtime)).append(SEPARATOR);
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
            var status = new AtomicInteger(0);
            var killStatus = sh(KILL_EMUS);
            status.addAndGet(killStatus);
            for (Device device : getDeviceList().getResponse()) {
                var deleteStatus = sh(String.format(DELETE_EMU, device.getName()));
                status.addAndGet(deleteStatus);
            }
            response
                    .setSuccess(status.get() == 0)
                    .setResponse(status.get() == 0 ? DELETION_SUCCESS : DELETION_FAILURE);
        } catch (IOException | InterruptedException e) {
            response
                    .setSuccess(false)
                    .setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseDTO<String> addContacts(String deviceName) {
        var response = new ResponseDTO<String>();
        return response.setSuccess(true);
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
            var status = sh(String.format("adb -s %s uninstall %s", device.get().getId(), bundleId));
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
    public ResponseDTO<String> killWDASession(String device) {
        var response = new ResponseDTO<String>();
        return response.setSuccess(true);
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
            int exitCode = sh(String.format("adb -s %s shell ls %s", device.get().getId(), filePath));
            response
                    .setSuccess(true)
                    .setResponse(exitCode == 0);
        } catch (IOException | InterruptedException e) {
            response
                    .setSuccess(false)
                    .setResponse(false)
                    .setMessage(e.getMessage());
        }
        return response;
    }
}
