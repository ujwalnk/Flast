package in.madilu.flast.sentinel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class ConfigSentinel implements Serializable {

    // Auto Open App Paths
    private ArrayList<String> listOpenApps = new ArrayList<>();
    private static final String CONFIG_FILE_PATH = "./config.txt";
    private static final Logger L = Supplement.readyLogger(Logger.getLogger("in.Madilu.Flast.Sentinel.ConfigSentinel"));

    public ConfigSentinel() {
        try {
            setApps2Open(Arrays.asList(FileSentinel.readFile(CONFIG_FILE_PATH).toString().split("\n")));
        } catch (Exception e) {
            L.warning("Unable to Read from Log File");
            L.finest(e.toString());
        }
    }

    /**
     * Add Executable to auto open 
     * @param path Path to executable
     */
    public void add2Apps2Open(String path) {
        listOpenApps.add(path);
        commitConfig();
    }

    /**
     * Commit local configuration to configuration file
     */
    private void commitConfig() {
        try {
            listOpenApps.removeAll(Arrays.asList("", null));
            StringBuilder lsvBuilder = new StringBuilder();
            for (String app : listOpenApps) {
                if(!app.trim().isEmpty()){
                lsvBuilder.append(app).append("\n");
                }   
            }
            FileSentinel.writeFile(CONFIG_FILE_PATH, lsvBuilder.toString());
        } catch (Exception e) {
            L.warning("Unable to write to config file");
            L.finest(e.toString());
        }
    }

    /**
     * Get list of auto open apps
     * @return ArrayList<String> of auto open app Paths
     */
    public ArrayList<String> getApps2Open() {
        return listOpenApps;
    }

    /**
     * Set list of auto open apps, clears existing paths
     * @param listOpenApps ArrayList<String> of executable paths
     */
    public void setApps2Open(List<String> listOpenApps) {
        this.listOpenApps = new ArrayList<>(listOpenApps);
        commitConfig();
    }
}
