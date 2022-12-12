package manager.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringJoiner;

public class ShellExecutorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShellExecutorService.class);

    private static final File HOME_DIRECTORY = new File(System.getProperty("user.home"));

    private static Process startProcess(String command) throws IOException {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("sh", "-c", command);
        builder.directory(HOME_DIRECTORY);
        LOGGER.info("Executing command: " + command);
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
        LOGGER.info("Command \"" + command + "\" output:\n" + output);
        return output.toString();
    }
}
