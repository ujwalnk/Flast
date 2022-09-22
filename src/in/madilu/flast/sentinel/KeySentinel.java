package in.madilu.flast.sentinel;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class KeySentinel {

    private String userName = "";
    private String password = "";

    private static final Logger LOG = Supplement.readyLogger(Logger.getLogger("in.Madilu.Flast.Sentinel.KeySentinel"));

    private static final String KEY_FILE_PATH = "./.key";
    private static final String SECRET_FILE_PATH = "./.secret";
    private static final Random r = new Random();

    private ByteArrayOutputStream credStream = new ByteArrayOutputStream();

    public KeySentinel() throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException,
            NoSuchPaddingException, IOException {

        // Create Secret File if not exists
        File secretFile = new File(SECRET_FILE_PATH);
        if (secretFile.createNewFile()) {
            LOG.warning("Credentials file missing, create empty file");
        }

        try (FileInputStream fileInputStream = new FileInputStream(secretFile);) {

            // Generate Key if not already generated
            try {
                FileSentinel.readFile(KEY_FILE_PATH);
            } catch (IOException e) {
                LOG.warning("Key File not found, generating key file");
                FileSentinel.writeFile(KEY_FILE_PATH, generateKey());
            }

            // Get Credentials from file
            getCredentials();
        }

        // Log and throw all exceptions

        catch (SecurityException e) {
            LOG.severe("Security Exception, unable to access Credentials file");
            LOG.finest(e.toString());
            throw e;
        } catch (FileNotFoundException e) {
            LOG.warning("Credentials File not Found");
            LOG.finest(e.toString());
            throw e;
        } catch (Exception e) {
            LOG.finest(e.toString());
        }

    }

    /**
     * Commit Credentials from local variable to file
     * 
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws IOException
     */
    public void setCredentials() throws InvalidKeyException, NoSuchAlgorithmException,
            InvalidKeySpecException, NoSuchPaddingException, IOException {
        try (BufferedWriter bf = Files.newBufferedWriter(Path.of(SECRET_FILE_PATH),
                StandardOpenOption.TRUNCATE_EXISTING)) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        encryptOrDecrypt(FileSentinel.readFile(KEY_FILE_PATH).toString(), Cipher.ENCRYPT_MODE,
                new ByteArrayInputStream((userName + "," + password).getBytes()),
                new FileOutputStream(SECRET_FILE_PATH));
    }

    /**
     * Get Local Credentials
     * 
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws IOException
     */
    public void getCredentials() throws InvalidKeyException, NoSuchAlgorithmException,
            InvalidKeySpecException, NoSuchPaddingException, IOException {
        credStream.flush();
        credStream.reset();
        encryptOrDecrypt(FileSentinel.readFile(KEY_FILE_PATH).toString(), Cipher.DECRYPT_MODE,
                new FileInputStream(SECRET_FILE_PATH), credStream);

        final String CRED_STRING = credStream.toString();
        if (!CRED_STRING.isEmpty()) {
            userName = CRED_STRING.substring(0, CRED_STRING.indexOf(","));
            password = CRED_STRING.substring(CRED_STRING.indexOf(",") + 1);
        } else{
            LOG.warning("Unable to read secret file, reset credentials in settings menu");
        }
    }

    /**
     * Encrypt or Decrypt credentials
     * 
     * @param key  String key for encryption
     * @param mode Encrypt ot Decrypt Mode
     * @param is   InputStream Source
     * @param os   OutputStream Destination
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws IOException
     */
    private void encryptOrDecrypt(String key, int mode, InputStream is, OutputStream os) throws InvalidKeyException,
            NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IOException {

        // Using DES Encryption
        DESKeySpec dks = new DESKeySpec(key.getBytes());
        SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
        SecretKey desKey = skf.generateSecret(dks);

        // Can use DES/ECB/PKCS5Padding for SunJCE
        Cipher cipher = Cipher.getInstance("DES");

        if (mode == Cipher.ENCRYPT_MODE) {
            cipher.init(Cipher.ENCRYPT_MODE, desKey);
            CipherInputStream cis = new CipherInputStream(is, cipher);
            doCopy(cis, os);
        } else if (mode == Cipher.DECRYPT_MODE) {
            cipher.init(Cipher.DECRYPT_MODE, desKey);
            CipherOutputStream cos = new CipherOutputStream(os, cipher);
            doCopy(is, cos);
        }
    }

    /**
     * Copy data from InputStream to OutputStream
     */
    private void doCopy(InputStream is, OutputStream os) throws IOException {
        try {
            byte[] bytes = new byte[64];
            int numBytes;
            while ((numBytes = is.read(bytes)) != -1) {
                os.write(bytes, 0, numBytes);
            }
            os.flush();
            os.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get username
     * 
     * @return Username String
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Get password
     * 
     * @return String Password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set Username
     * 
     * @param userName String username
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Set password
     * 
     * @param password String password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Generate random key
     * 
     * @return Random Key String
     */
    private String generateKey() {
        StringBuilder sb = new StringBuilder();
        while (sb.length() < 64) {
            sb.append(Integer.toHexString(r.nextInt()));
        }

        return sb.toString().substring(0, 64);
    }

    /**
     * Check for Credentials
     * 
     * @return Credential Available
     */
    public boolean isOK() {
        return !(userName.isEmpty() || password.isEmpty());
    }

}