package manager.controller;

import lombok.extern.slf4j.Slf4j;
import manager.dto.Device;
import manager.dto.ResponseDTO;
import manager.emulator.EmulatorFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class EmulatorManagementController {

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseDTO<List<Device>> getEmulatorList(@RequestParam String platform) {
        var response = new EmulatorFactory(platform.toLowerCase()).getEmulator()
                .getDeviceList();
        log.info(response.toString());
        return response;
    }

    @RequestMapping(value = "/create", method = RequestMethod.PUT)
    public ResponseDTO<String> createEmulators(@RequestParam String platform,
                                               @RequestParam String devices,
                                               @RequestParam String runtimes,
                                               @RequestParam(required = false) Boolean isHeadless) {
        var response = new EmulatorFactory(platform.toLowerCase()).getEmulator()
                .createAndRunEmulator(devices, runtimes, isHeadless);
        log.info(response.toString());
        return response;
    }

    @RequestMapping(value = "/delete/all", method = RequestMethod.DELETE)
    public ResponseDTO<String> deleteAllEmulators(@RequestParam String platform) {
        var response = new EmulatorFactory(platform.toLowerCase()).getEmulator()
                .deleteAllEmulators();
        log.info(response.toString());
        return response;
    }

    @RequestMapping(value = "/contacts", method = RequestMethod.PUT)
    public ResponseDTO<String> addContacts(@RequestParam String platform,
                                           @RequestParam String device) {
        var response = new EmulatorFactory(platform.toLowerCase()).getEmulator()
                .addContacts(device);
        log.info(response.toString());
        return response;
    }

    @RequestMapping(value = "/delete/app", method = RequestMethod.DELETE)
    public ResponseDTO<String> deleteApp(@RequestParam String platform,
                                         @RequestParam String device,
                                         @RequestParam String bundleId) {
        var response = new EmulatorFactory(platform.toLowerCase()).getEmulator()
                .deleteApp(device, bundleId);
        log.info(response.toString());
        return response;
    }

    @RequestMapping(value = "/delete/wda", method = RequestMethod.DELETE)
    public ResponseDTO<String> killWDASession(@RequestParam String platform,
                                              @RequestParam String device) {
        var response = new EmulatorFactory(platform.toLowerCase()).getEmulator()
                .killWDASession(device);
        log.info(response.toString());
        return response;
    }

    @RequestMapping(value = "/hasFile", method = RequestMethod.GET)
    public ResponseDTO<Boolean> isFileExistsAtEmulator(@RequestParam String platform,
                                                      @RequestParam String device,
                                                      @RequestParam String filePath) {
        var response = new EmulatorFactory(platform.toLowerCase()).getEmulator()
                .isFileExistsAtEmulator(device, filePath);
        log.info(response.toString());
        return response;
    }

}
