package manager.utils;

public class Constants {
    public static final String SEPARATOR = System.getProperty("line.separator");

    public static final String IOS = "ios";
    public static final String ANDROID = "android";

    public static final String EMU_AT_PROPERTY = "qemu.at.name";

    //region Command lines
    public static final String LIST_SIMS = "xcrun simctl list devices available --json";
    public static final String BOOT_SIM_COMMAND = "xcrun simctl boot %s";
    public static final String CREATE_SIM_COMMAND = "xcrun simctl create \"%s\" \"%s\" " +
            "'com.apple.CoreSimulator.SimRuntime.iOS-%s' --display=internal";
    public static final String DELETE_SIMS = "xcrun simctl delete all";

    public static final String ADB_DEVICES = "adb devices | grep emulator | cut -f1";
    public static final String ADB_GET_PROPERTY = "adb -s %s shell getprop %s";
    public static final String LIST_AVD = "~/Library/Android/sdk/cmdline-tools/latest/bin/avdmanager list avd";
    public static final String CREATE_EMU_COMMAND = "echo \"no\" | ~/Library/Android/sdk/cmdline-tools/latest/bin/avdmanager " +
            "create avd --force --name \"%s\" --package \"system-images;android-%s;google_apis;x86\" --tag \"google_apis\" --abi \"x86\"";
    public static final String RUN_EMU_COMMAND = "~/Library/Android/sdk/emulator/emulator @%s -gpu on -wipe-data -no-boot-anim -noaudio -no-snapshot %s -prop %s=%s " +
            "-memory 2048 -partition-size 4096 -skin 1080x2280 -qemu -lcd-density 420 &> /dev/null &";
    public static final String KILL_EMUS = "adb devices | grep emulator | cut -f1 | while read line; do adb -s $line emu kill; done";
    public static final String DELETE_EMU = "~/Library/Android/sdk/cmdline-tools/latest/bin/avdmanager delete avd -n \"%s\"";
    //endregion

    //region Strings and messages
    public static final String DELETION_SUCCESS = "Emulators successfully deleted";
    public static final String DELETION_FAILURE = "Something went wrong during emulators deletion";

    public static final String ADD_CONTACTS_SUCCESS = "Contacts successfully added";
    public static final String ADD_CONTACTS_FAILURE = "Something went wrong during contacts adding";

    public static final String DELETE_APP_SUCCESS = "Application %s successfully deleted";
    public static final String DELETE_APP_FAILURE = "Something went wrong during application %s deletion";

    public static final String INCORRECT_DEVICE_LIST = "Incorrect device and runtime lists size!";

    public static final String DEVICE_ADDED = "%s (%s) successfully added";
    public static final String DEVICE_FAILED = "Adding of %s (%s) failed with status %d";
    public static final String DEVICE_ALREADY_EXISTS = "%s (%s) already exists";
    public static final String DEVICE_BOOTED = "%s (%s) has been booted";
    public static final String DEVICE_NOT_BOOTED = "%s (%s) couldn't boot, check runtimes";

    public static final String RUN_EMULATOR = "Run emulator %s (%s)";
    //endregion
}
