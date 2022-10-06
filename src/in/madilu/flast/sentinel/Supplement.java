package in.madilu.flast.sentinel;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Supplement {

    private static FileHandler fileHandler;
    private static SimpleFormatter formatter = new SimpleFormatter();

    private Supplement() {
    }

    /**
     * Initialize FileHandler - Logging to File
     */
    static {
        try {
            fileHandler = new FileHandler("./.log", true);
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize Logger object
     * 
     * @param logger - Logger to Initialize
     * @return Logger
     */
    public static Logger readyLogger(Logger logger) {
        // Adding FileFormatter
        logger.addHandler(fileHandler);
        
        fileHandler.setFormatter(formatter);

        // Remove Console Handler
        logger.setUseParentHandlers(false);

        File f = new File("./.dev");
        if (f.exists() && !f.isDirectory()) {
            logger.setLevel(Level.FINEST);
        }

        return logger;
    }

    private static final Logger LOG = readyLogger(Logger.getLogger("in.Madilu.Flast.Sentinel.Suppliment"));

    /**
     * Open Link on System default Browser
     * 
     * @param url - Webpage URL
     */
    public static void openLink(String url) {
        // Check for supported Desktops (Windows & Mac)
        if (Desktop.isDesktopSupported() && System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(new URI(url));
                } catch (IOException | URISyntaxException e) {
                    LOG.finest(e.toString());
                    LOG.warning("Unable to open Browser on Desktop (URL: \'" + url + "\')");
                }
            }
        }
        // Linux Implementation
        else {
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec("xdg-open " + url);
            } catch (IOException e) {
                LOG.finest(e.toString());
                LOG.warning("Unable to open Browser on XDG (URL: \'" + url + "\')");
            }
        }
    }

    /**
     * Open User Choice Apps on system
     */
    public static void openApps() {
        try {
            ConfigSentinel cSentinel = new ConfigSentinel();
            for (String s : cSentinel.getApps2Open().toArray(new String[cSentinel.getApps2Open().size()])) {
                Runtime.getRuntime().exec(s);
            }
        } catch (IOException e) {
            LOG.warning("Unable to to open apps");
            LOG.finest(e.toString());
        } catch (StringIndexOutOfBoundsException ignore) {
            // User has not added any apps to auto start, Ignore Exception
        }
    }
}
