package cs451;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;

public class OutputWriter {

    private String output;
    private String out = "b";
    private String in = "d";

    public OutputWriter(String output) {
        this.output = output;
        // Create (if missing) and truncate the file immediately
        try (BufferedWriter bw = Files.newBufferedWriter(
                Paths.get(output),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,        // create if absent
                StandardOpenOption.TRUNCATE_EXISTING // truncate to 0 bytes
        )) {
            // no initial content; just opening with TRUNCATE_EXISTING clears the file
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeRecieve(int port, int msg){
        String line = in + " " + port + " " + msg;

        // Escribo en paginas hosts
        try {
            // Append the string as a new line
            Files.write(
                    Paths.get(output),
                    Collections.singletonList(line),
                    StandardOpenOption.CREATE,     // create file if it doesn't exist
                    StandardOpenOption.APPEND      // append to the end
            );
            System.out.println("Line appended successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeSend(int msg){
        String line = out + " " + msg;

        // Escribo en paginas hosts
        try {
            // Append the string as a new line
            Files.write(
                    Paths.get(output),
                    Collections.singletonList(line),
                    StandardOpenOption.CREATE,     // create file if it doesn't exist
                    StandardOpenOption.APPEND       // append to the end
            );
            System.out.println("Line appended successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
