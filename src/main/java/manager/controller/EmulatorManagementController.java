package manager.controller;

import manager.dto.Device;
import manager.dto.ResponseDTO;
import manager.emulator.EmulatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EmulatorManagementController {

    private final Logger LOGGER = LoggerFactory.getLogger(EmulatorManagementController.class);

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseDTO<List<Device>> getEmulatorList(@RequestParam String platform) {
        var response = new EmulatorFactory(platform.toLowerCase()).getEmulator()
                .getDeviceList();
        LOGGER.info(response.toString());
        return response;
    }

    @RequestMapping(value = "/create", method = RequestMethod.PUT)
    public ResponseDTO<String> createEmulators(@RequestParam String platform,
                                               @RequestParam String devices,
                                               @RequestParam String runtimes,
                                               @RequestParam(required = false) Boolean isHeadless) {
        var response = new EmulatorFactory(platform.toLowerCase()).getEmulator()
                .createAndRunEmulator(devices, runtimes, isHeadless);
        LOGGER.info(response.toString());
        return response;
    }

    @RequestMapping(value = "/delete/all", method = RequestMethod.DELETE)
    public ResponseDTO<String> deleteAllEmulators(@RequestParam String platform) {
        var response = new EmulatorFactory(platform.toLowerCase()).getEmulator()
                .deleteAllEmulators();
        LOGGER.info(response.toString());
        return response;
    }

    @RequestMapping(value = "/contacts", method = RequestMethod.PUT)
    public ResponseDTO<String> addContacts(@RequestParam String platform,
                                           @RequestParam String device) {
        var response = new EmulatorFactory(platform.toLowerCase()).getEmulator()
                .addContacts(device);
        LOGGER.info(response.toString());
        return response;
    }

    @RequestMapping(value = "/delete/app", method = RequestMethod.DELETE)
    public ResponseDTO<String> deleteApp(@RequestParam String platform,
                                         @RequestParam String device,
                                         @RequestParam String bundleId) {
        var response = new EmulatorFactory(platform.toLowerCase()).getEmulator()
                .deleteApp(device, bundleId);
        LOGGER.info(response.toString());
        return response;
    }

    @RequestMapping(value = "/delete/wda", method = RequestMethod.DELETE)
    public ResponseDTO<String> killWDASession(@RequestParam String platform,
                                              @RequestParam String device) {
        var response = new EmulatorFactory(platform.toLowerCase()).getEmulator()
                .killWDASession(device);
        LOGGER.info(response.toString());
        return response;
    }

    @RequestMapping(value = "/hasFile", method = RequestMethod.GET)
    public ResponseDTO<Boolean> isFileExistsAtEmulator(@RequestParam String platform,
                                                      @RequestParam String device,
                                                      @RequestParam String filePath) {
        var response = new EmulatorFactory(platform.toLowerCase()).getEmulator()
                .isFileExistsAtEmulator(device, filePath);
        LOGGER.info(response.toString());
        return response;
    }

}
