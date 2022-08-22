package in.madilu.flast;

import java.io.File;
import java.io.PrintWriter;

import in.madilu.flast.uifactory.Interface;

public class Flast {

    public static void main(String[] args) throws Exception {
        monitorLogFile();

        // Set Logger Format
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");

        // Start Interface
        new Interface();
    }

    /**
     * Clear Log file Contents it file exceeds 1000 KB
     */
    private static void monitorLogFile() {
        File logFile = new File("./.log");
        try {
            if (logFile.length() / (1024) > 1000) {
                PrintWriter logWriter = new PrintWriter(logFile);
                logWriter.print("");
                logWriter.close();
            }
        } catch (Exception ignore) {
        }
    }
}
