package in.madilu.flast.uifactory;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Logger;

import javax.crypto.NoSuchPaddingException;
import javax.imageio.ImageIO;
import javax.net.ssl.SSLHandshakeException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.MouseInputAdapter;

import in.madilu.flast.sentinel.KeySentinel;
import in.madilu.flast.sentinel.NetSentinel;
import in.madilu.flast.sentinel.Supplement;

public class PaintMain {

    private JLabel statusLabel;
    private JLabel statusImage;

    private final JButton connectBtn;
    private final JButton disconnectBtn;
    private final JButton configureBtn;
    private JButton contributeOrUpdateBtn;

    private JLabel textLabel;

    private static final int IMG_CONNECTED = 0;
    private static final int IMG_DISCONNECTED = 1;
    private static final int IMG_FAIL = 2;
    private static final int IMG_PROCESSING = 4;

    private static final Logger LOG = Supplement.readyLogger(Logger.getLogger("in.Madilu.Flast.UiFactory.PaintMain"));

    public PaintMain(JPanel panel) throws IOException {
        panel.setBackground(Color.WHITE);

        // Connect Button
        connectBtn = Tailor.tailorButton("Connect", "#4aaa4a", 134, 48, 450, 40, panel);
        connectBtn.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
        connectBtn.addActionListener(e -> {
            if (NetSentinel.isWiFiConnected()) {
                changeStatusImg(IMG_PROCESSING);
                try {
                    LOG.finest("Connection Status:" + String.valueOf(NetSentinel.testConnection()));
                    if (!NetSentinel.testConnection()) {
                        if (NetSentinel.connect()) {
                            changeStatusImg(IMG_CONNECTED);
                            Supplement.openApps();
                        } else {
                            changeStatusImg(IMG_FAIL);
                        }
                    } else {
                        changeStatusImg(IMG_CONNECTED);
                        Supplement.openApps();
                    }
                } catch (SSLHandshakeException ex) {
                    LOG.warning("Incorrect Credentials");
                    LOG.finest(ex.toString());
                    changeStatusImg(IMG_FAIL, "Incorrect Credentials");
                } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException
                        | NoSuchPaddingException | IOException e1) {
                    LOG.severe("Unable to Connect");
                    LOG.finest(e1.toString());
                }
            } else {
                JFrame jFrame = new JFrame();
                JOptionPane.showMessageDialog(jFrame, "Internet Not Available\nCheck your WiFi / Ethernet connection",
                        "Internet Not Available",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
        connectBtn.setMnemonic('c');

        // Disconnect Button
        disconnectBtn = Tailor.tailorButton("Disconnect", "#ff4040", 134, 48, 450, 99, panel);
        disconnectBtn.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
        disconnectBtn.addActionListener(e -> {
            // TODO: Check for connected to WiFi
            changeStatusImg(IMG_PROCESSING);
            try {
                if (NetSentinel.disconnect()) {
                    changeStatusImg(IMG_DISCONNECTED);
                } else {
                    changeStatusImg(IMG_FAIL);
                }
            } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException
                    | IOException e1) {
                LOG.severe("Unable to disconnect");
                LOG.finest(e1.toString());
            }
        });
        disconnectBtn.setMnemonic('d');

        // Configure Button
        configureBtn = Tailor.tailorButton("Configure", "#1e85da", 134, 48, 450, 180, panel);
        configureBtn.addActionListener(e -> Interface.showConfig());

        // Web Button
        contributeOrUpdateBtn = Tailor.tailorButton("Contribute", "#1e85da", 134, 48, 450,
                239, panel);
        contributeOrUpdateBtn.addActionListener(e -> Supplement.openLink("https://github.com/ujwalnk/Flast"));

        // Static Image Label
        BufferedImage imageData = ImageIO.read(new File("./res/disconnected.png"));
        statusImage = new JLabel(new ImageIcon(imageData));
        statusImage.setSize(436, 436);
        statusImage.setLocation(0, -52);
        panel.add(statusImage);

        // Status Label
        statusLabel = Tailor.tailorLabel("", 410, 66, 18, 290, panel);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Credits Button
        textLabel = Tailor.tailorLabel("brought to you from MADILU", 620, 20, 0, 350, panel);
        textLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        textLabel.setForeground(Color.BLUE);
        textLabel.setHorizontalAlignment(SwingConstants.CENTER);
        textLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 16));
        textLabel.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Supplement.openLink("https://www.madilu.in");
            }
        });

        startup();
    }

    /*
     * Change the License Button to Update, update parameters
     */
    public void changeButton2Update() {
        if (NetSentinel.check4Updates()) {
            contributeOrUpdateBtn.setText("Update");
            contributeOrUpdateBtn
                    .addActionListener(e -> Supplement.openLink("https://www.github.com/ujwalnk/Flast/releases"));
        }
    }

    /**
     * Change Status Image
     * 
     * @param status - Status Number
     * @throws IOException - File Not Found
     */
    private void changeStatusImg(int status) {
        try {
            if (status == IMG_DISCONNECTED) {
                statusImage.setIcon(new ImageIcon(ImageIO.read(new File("./res/disconnected.png"))));
                statusLabel.setText("Disconnected");
            } else if (status == IMG_CONNECTED) {
                statusImage.setIcon(new ImageIcon(ImageIO.read(new File("./res/success.png"))));
                statusLabel.setText("Connected");
            } else if (status == IMG_PROCESSING) {
                statusLabel.setText("Please Wait");
            } else {
                statusImage.setIcon(new ImageIcon(ImageIO.read(new File("./res/fail.png"))));
                statusLabel.setText("Error Encountered. View Log for details");
            }
        } catch (IOException e) {
            LOG.severe("Encountered IO Exception: Unable to access res files");
        }
    }

    /**
     * Change status label text and image
     * 
     * @param status - Integer Status Number
     * @param s      - Message to be displayed
     */
    private void changeStatusImg(int status, String s) {
        changeStatusImg(status);
        statusLabel.setText(s);
    }

    /**
     * Change the Status label text
     */
    public void changeStatusText(String s) {
        statusLabel.setText(s);
    }

    /**
     * Routine calls on Application Interface Start
     */
    private void startup() {
        changeStatusImg(IMG_PROCESSING);

        // When Credentials not available, open config menu on open
        KeySentinel keySentinel;
        try {
            keySentinel = new KeySentinel();
            keySentinel.getCredentials();
            if (!keySentinel.isOK() || keySentinel.getPassword().isEmpty() || keySentinel.getUserName().isEmpty()) {
                // TODO: Config Not openineg
            }
        } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException
                | IOException ignore) {
            LOG.severe("User credentials missing, Config Menu Open");
        }

        if (NetSentinel.isWiFiConnected()) {
            try {
                if (!NetSentinel.testConnection()) {
                    if (NetSentinel.connect()) {
                        changeStatusImg(IMG_CONNECTED);
                        Supplement.openApps();
                    } else {
                        changeStatusImg(IMG_FAIL);
                    }
                } else {
                    changeStatusImg(IMG_CONNECTED);
                    Supplement.openApps();
                }
            } catch (SSLHandshakeException ex) {
                LOG.warning("Incorrect Credentials");
                LOG.finest(ex.toString());
                changeStatusImg(IMG_FAIL, "Incorrect Credentials");
            } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException
                    | IOException e1) {
                LOG.severe("Unable to Connect");
                LOG.finest(e1.toString());
            }

            changeButton2Update();
        } else {
            JFrame jFrame = new JFrame();
            JOptionPane.showMessageDialog(jFrame, "Internet Not Available\nCheck your WiFi / Ethernet connection",
                    "Internet Not Available",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
}
