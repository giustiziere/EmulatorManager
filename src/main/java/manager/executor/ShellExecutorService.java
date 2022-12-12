package manager.executor;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringJoiner;

@Slf4j
public class ShellExecutorService {

    private static final File HOME_DIRECTORY = new File(System.getProperty("user.home"));

    private static Process startProcess(String command) throws IOException {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("sh", "-c", command);
        builder.directory(HOME_DIRECTORY);
        log.info("Executing command: " + command);
        return builder.start();
    }

    public static int sh(String command) throws IOException, InterruptedException {
        Process process = startProcess(command);
        process.waitFor();
        return process.exitValue();
    }

    public static void shNoWait(String command) throws IOException {
        startProcess(command);
    }

    public static String shOut(String command) throws IOException, InterruptedException {
        Process process = startProcess(command);
        StringJoiner output = new StringJoiner("\n");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.add(line);
            }
        }
        process.waitFor();
        log.info("Command \"" + command + "\" output:\n" + output);
        return output.toString();
    }
}
