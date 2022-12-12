package manager.emulator;

import manager.dto.Device;
import manager.dto.ResponseDTO;

import java.util.List;
import java.util.Optional;

public interface IEmulator {

    ResponseDTO<List<Device>> getDeviceList();

    ResponseDTO<String> createAndRunEmulator(String deviceNames, String runtimes, Boolean isHeadless);

    ResponseDTO<String> deleteAllEmulators();

    ResponseDTO<String> addContacts(String deviceName);

    ResponseDTO<String> deleteApp(String deviceName, String bundleId);

    ResponseDTO<String> killWDASession(String device);

    ResponseDTO<Boolean> isFileExistsAtEmulator(String deviceName, String filePath);

    default boolean isEmulatorExist(String deviceName, String runtime) {
        return getDeviceList().getResponse().stream()
                .anyMatch(device -> device.getName().equals(deviceName) && device.getOsVersion().equals(runtime));
    }

    default boolean isEmulatorBooted(String deviceName, String runtime) {
        var device = getDeviceByNameAndRuntime(deviceName, runtime);
        return device.isPresent() && device.get().getStatus().equals("Booted");
    }

    default Optional<Device> getDeviceByNameAndRuntime(String deviceName, String runtime) {
        return getDeviceList().getResponse().stream()
                .filter(v -> v.getName().equals(deviceName) && v.getOsVersion().equals(runtime))
                .findFirst();
    }
}
