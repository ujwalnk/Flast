package in.madilu.flast.sentinel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Logger;

import javax.crypto.NoSuchPaddingException;
import javax.net.ssl.SSLHandshakeException;

import in.madilu.flast.VersionNumber;

public class NetSentinel {
    private static boolean isConnected = false;
    private static final Logger LOG = Supplement.readyLogger(Logger.getLogger("in.Madilu.Flast.Sentinel.NetOperator"));

    public static String NOT_SET = "NOT_SET";

    private NetSentinel() {
    }

    /**
     * Send connect POST Request to Server
     * 
     * @return Boolean successful
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws IOException
     * @throws WrongCredentialException
     */
    public static boolean connect() throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException,
            NoSuchPaddingException, IOException, SSLHandshakeException {
        KeySentinel keySentinel;
        try {
            // Create KeySentinel Object
            keySentinel = new KeySentinel();
        } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException
                | IOException e) {
            LOG.severe("Unable to Initialize KeySentinel");
            LOG.finest(e.toString());
            throw e;
        }
        keySentinel.getCredentials();

        // Mode = 191 : Login Command
        // username
        // password
        // a = system current epoch time

        if (keySentinel.isOK()) {
            try {
                // Check Server Response
                if (sendRequest(getWalledGarden() + "login.xml", ("mode=191&username=" + keySentinel.getUserName()
                        + "&password=" + keySentinel.getPassword()
                        + "&a=" + String.valueOf(System.currentTimeMillis()))).indexOf(
                                "<?xml version='1.0' ?><requestresponse><status><![CDATA[LIVE]]></status><message><![CDATA[You are signed in as {username}]]></message><logoutmessage><![CDATA[You have successfully logged off]]></logoutmessage><state><![CDATA[]]></state></requestresponse>") != -1) {
                } else {
                    isConnected = false;
                }
            } catch (SSLHandshakeException e) {
                LOG.warning("Incorrect Credentials");
                throw e;
            }
        }
        isConnected = testConnection();
        return isConnected;
    }

    /**
     * Send disconnect POST Request to Server
     * 
     * @return disconnect success
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws IOException
     * @throws WrongCredentialException
     */
    public static boolean disconnect() throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException,
            NoSuchPaddingException, IOException {
        KeySentinel keySentinel;
        try {
            // Create a new KeySentinel
            keySentinel = new KeySentinel();
        } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException
                | IOException e) {
            LOG.severe("Unable to Initialize KeySentinel");
            LOG.finest(e.toString());
            throw e;
        }
        keySentinel.getCredentials();

        // Check Server Response
        if (keySentinel.isOK()) {
            if (sendRequest(getWalledGarden() + "login.xml", ("mode=193&username=" + keySentinel.getUserName()
                    + "&a=" + String.valueOf(System.currentTimeMillis()))).indexOf(
                            "<?xml version='1.0' ?><requestresponse><status><![CDATA[LOGIN]]></status><message><![CDATA[You&#39;ve signed out]]></message></requestresponse>") != -1) {
                isConnected = true;
                LOG.info("Disconnect Succcess");
            } else {
                isConnected = false;
                LOG.warning("Disconnect Fail");
            }
        } else {
            LOG.severe("No Credentials to Disconnect");
        }
        isConnected = testConnection();
        return !isConnected;

    }

    /**
     * Send POST Request to URL
     * 
     * @param url  - URL
     * @param json - POST data
     * @return String - Sever Response
     *         empty string on no response
     * @throws SSLHandshakeException
     * @throws WrongCredentialException
     * @throws IOException
     */
    private static String sendRequest(String url, String json) throws SSLHandshakeException {
        LOG.info(() -> String.format("Sending POST request to %s", url));

        // Create a HTTP Connection to URL
        try {
            URL postUrl = new URL(url);

            HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Stream data to Output stream
            OutputStream stream = connection.getOutputStream();
            stream.write(json.getBytes());
            stream.flush();
            stream.close();

            // Get Response code
            int responseCode = connection.getResponseCode();
            LOG.info(() -> String.format("POST Response Code: %s", responseCode));

            // If Response OKAY
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response into the string using BufferedReader
                BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputString;
                StringBuilder builder = new StringBuilder();

                while ((inputString = responseReader.readLine()) != null) {
                    builder.append(inputString);
                }

                // Close BufferedReader and return String
                responseReader.close();
                LOG.finest(() -> String.format("Server Response: %s", builder.toString()));
                return builder.toString();
            } else {
                LOG.warning("No Response from Server");
                throw new SSLHandshakeException("");
            }
        } catch (SSLHandshakeException e) {
            LOG.warning("Incorrect Credentials");
            throw e;
        } catch (IOException e) {
            LOG.severe("Unable to send Request to Server");
            LOG.finest(e.toString());
            return "";
        }
    }

    /**
     * Get Walled Garden Address
     * 
     * @return String - URL of Walled Garden
     */
    private static String getWalledGarden() {
        return "http://192.168.254.1:8090/";
    }

    /**
     * Check Internet Connection
     * 
     * @return (Boolean) Internet Connection Status
     */
    public static boolean testConnection() {
        try {
            return getData("https://ujwalnk.github.io/Flast/success.txt").equals("success");
        } catch (SSLHandshakeException e) {
            return false;
        }
    }

    /**
     * Get Latest Version Number
     * 
     * @return String - Latest Version Number
     */
    public static String getLatestVersion() {
        try {
            return getData("https://ujwalnk.github.io/Flast/version.txt");
        } catch (SSLHandshakeException e) {
            return null;
        }
    }

    /**
     * Get data from given URL
     * 
     * @param url The URL String of the webpage
     * @return String - Webpage data
     * @throws SSLHandshakeException
     */
    public static String getData(String url) throws SSLHandshakeException {

        StringBuilder returnStringBuilder = new StringBuilder();

        try {
            // Open Connection to URL
            // TODO: Set timeout limit
            URL u = new URL(url);
            URLConnection uConnection = u.openConnection();
            uConnection.setReadTimeout(5000);
            uConnection.setConnectTimeout(5000);

            // Read data from Connection into String
            BufferedReader reader = new BufferedReader(new InputStreamReader(uConnection.getInputStream()));
            String lineString;

            while ((lineString = reader.readLine()) != null) {
                returnStringBuilder.append(lineString + '\n');
            }

            reader.close();
        } catch (SSLHandshakeException e) {
            throw e;
        } catch (Exception e) {
            LOG.finest(e.toString());
            LOG.warning("Unable to access the Internet");
        }

        return returnStringBuilder.toString().trim();
    }

    /**
     * Check for new release of Application
     * 
     * @return Boolean - True on Update available
     */
    public static boolean check4Updates() {
        String latestVersionNumber = NetSentinel.getLatestVersion();
        try {
            if (!latestVersionNumber.equals(VersionNumber.VERSION) && !latestVersionNumber.isEmpty()) {
                LOG.info(() -> String.format("App Update Available %s to %s", VersionNumber.VERSION,
                        latestVersionNumber));
                return true;
            }
        } catch (NullPointerException ignore) {
            return false;
        }
        return false;
    }

    /**
     * Check if Device is connected over WiFi or Ethernet
     * @return
     */
    public static boolean isWiFiConnected() {
        try {
            if(System.getProperty("os.name").toLowerCase().contains("win")){
                if(getConnectedSSID().equals(NOT_SET)){
                    return false;
                }
                return true;
            }
            NetworkInterface.getByIndex(3).getName();
            return true;
        } catch (Exception networkNotFoundException) {
            LOG.warning(networkNotFoundException.toString());
            return false;
        }
    }

    private  static String getConnectedSSID(){
        String ssid = NOT_SET;
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "cmd.exe", "/c", "netsh wlan show interfaces");
            builder.redirectErrorStream(true);
            Process p = builder.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = r.readLine())!=null) {
                if (line.contains("SSID")){
                    ssid = line.split("\\s+")[3];
                    return ssid;
                }
            }
        } catch (IOException ex) {
            LOG.warning(ex.toString());
        }
        return ssid;
    }
}
