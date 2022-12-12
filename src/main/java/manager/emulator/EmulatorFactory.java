package manager.emulator;

import lombok.Getter;

import static manager.utils.Constants.ANDROID;
import static manager.utils.Constants.IOS;

@Getter
public class EmulatorFactory {
    private final IEmulator emulator;

    public EmulatorFactory(String platform) {
        switch (platform.toLowerCase()) {
            case IOS:
                emulator = new IOSSimulatorService();
                break;
            case ANDROID:
                emulator = new AndroidEmulatorService();
                break;
            default:
                throw new IllegalArgumentException();
        }
    }
}
