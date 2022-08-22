package in.madilu.flast.sentinel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

public class FileSentinel {

    private static final Logger L = Supplement.readyLogger(Logger.getLogger("in.Madilu.Flast.sentinel.FileSentinel"));

    private FileSentinel() {
    }

    /**
     * Read file on system
     * @param path the path of the file
     * @return A StringBuilder containing the file content
     * @throws IOException - If an I/O error occurs
     */
    public static StringBuilder readFile(String path) throws IOException {
        // Storing all lines in a StringBuilder
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = reader.readLine(); // Read lines
            while (line != null) {
                
                // Append line to StringBuilder
                stringBuilder.append(line);
                stringBuilder.append('\n');

                // Read next line from the file
                line = reader.readLine();
            }
        } catch(IOException e){
            L.warning("Unable to read file on path \"" + path + "\"");
            L.finest(e.toString());
            throw e;
        }
        return stringBuilder;
    }

    /**
     * Write String data to file
     * @param path File Path
     * @param s String to be written
     * @throws IOException If I/O error occurs
     */
    public static void writeFile(String path, String s) throws IOException {
        // creating the instance of file
        File filePath = new File(path);

        try (FileWriter writer = new FileWriter(filePath);) {

            // Write file to String
            writer.write(s);

        } catch (IOException e) {

            // Log and throw the Exception
            L.warning("Unable to write \"" + s + "\" to file on path \"" + path + "\"");
            L.finest(e.toString());
            throw e;

        }
    }
}
