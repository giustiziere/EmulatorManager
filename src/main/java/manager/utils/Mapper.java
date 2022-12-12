package manager.utils;

import manager.dto.Device;
import manager.dto.ios.IOSDeviceListDTO;

import java.util.ArrayList;
import java.util.List;

import static manager.utils.Constants.ANDROID;
import static manager.utils.Constants.IOS;
import static manager.utils.Utils.regexMatcher;

public class Mapper {
    public static List<Device> getIOSDeviceListDTOMapper(IOSDeviceListDTO dto) {
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

    public static List<Device> getAndroidDeviceListDTOMapper(String avdList) {
        var list = new ArrayList<Device>();
        var names = regexMatcher("\\s+Name:\\s(.+)\n", avdList, 1);
        for (int i = 0; i < names.size(); i++) {
            var osVersionString = regexMatcher("\\s+Based on:\\s(.+)\n", avdList, 1).get(i);
            list.add(new Device()
                    .setPlatform(ANDROID)
                    .setName(names.get(i))
                    .setOsVersion(regexMatcher("Android (\\d+.\\d+)", osVersionString, 1).get(0))
            );
        }
        return list;
    }
}
