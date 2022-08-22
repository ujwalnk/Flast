package in.madilu.flast.uifactory;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.crypto.NoSuchPaddingException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.MouseInputAdapter;

import in.madilu.flast.sentinel.ConfigSentinel;
import in.madilu.flast.sentinel.KeySentinel;
import in.madilu.flast.sentinel.Supplement;

public class PaintConfig extends JFrame implements ActionListener {

    private JTextField usernameField;

    private JLabel passLabel;
    private JPasswordField passField;

    private JButton licenseBtn;
    private JButton addAppsBtn;
    private JButton editAppsBtn;
    private JButton saveBtn;
    private JButton cancelBtn;

    private static final String DEFAULT_BTN_CLR = "#D9D9D9";

    private static final Logger LOG = Supplement.readyLogger(Logger.getLogger("in.Madilu.Flast.UIFactory.PaintConfig"));
    private transient KeySentinel keySentinel;

    public PaintConfig(JPanel panel) {
        try {
            keySentinel = new KeySentinel();
        } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException
                | IOException e) {
            LOG.warning("Unable to get Credentials");
            LOG.finest(e.toString());
        }

        panel.removeAll();
        panel.repaint();

        // JPanel properties
        setBounds(300, 300, 396, 420);
        getContentPane().setBackground(Color.WHITE);

        panel.setLayout(null);
        panel.setBackground(Color.WHITE);

        // Adding Elements to the content Pane

        // Labels
        Tailor.tailorLabel("UserName", 101, 42, 33, 44, panel);
        passLabel = Tailor.tailorLabel("Password", 101, 42, 33, 160, panel);
        passLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        passLabel.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (passField.getEchoChar() != '⚫') {
                    passField.setEchoChar('⚫');
                } else {
                    passField.setEchoChar((char) 0);
                }
            }
        });

        // Add App Button
        addAppsBtn = Tailor.tailorButton("Add App", DEFAULT_BTN_CLR, 115, 42, 447, 325, panel);
        addAppsBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(panel) == JFileChooser.APPROVE_OPTION) {
                ConfigSentinel cSentinel = new ConfigSentinel();
                cSentinel.add2Apps2Open(String.valueOf(chooser.getSelectedFile().toPath()));
            }
        });

        // Edit App Button
        editAppsBtn = Tailor.tailorButton("Edit Apps", DEFAULT_BTN_CLR, 115, 42, 33, 325, panel);
        editAppsBtn.addActionListener(e -> {
            Desktop desktop = Desktop.getDesktop();
            File configFile = new File("./config.txt");
            if (desktop.isSupported(Desktop.Action.EDIT)) {
                try {
                    desktop.edit(configFile);
                } catch (IOException ex) {
                    LOG.finest(ex.toString());
                    JOptionPane.showMessageDialog(null, String.format(
                            "Oops! We are unable to Open Config File. \nOpen the file  in your choice of text editor\n%s",
                            configFile.toURI().getPath()), "Flast | Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                LOG.warning("Unable to open Edit Apps File for User");
                JOptionPane.showMessageDialog(null, String.format(
                        "Oops! We are unable to Open Config File. \nOpen the file  in your choice of text editor\n%s",
                        configFile.toURI().getPath()), "Flast | Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        });

        // Cancel Button
        cancelBtn = Tailor.tailorButton("Cancel", DEFAULT_BTN_CLR, 144, 41, 418, 165, panel);
        cancelBtn.addActionListener(e -> Interface.showHome());

        // Save Button
        saveBtn = Tailor.tailorButton("Save", "#1E85DA", 144, 41, 418, 107, panel);
        saveBtn.addActionListener(e -> {
            saveFunction();
            Interface.showHome();
        });

        // License Button
        licenseBtn = Tailor.tailorButton("License", "#1E85DA", 241, 42, 177, 325,
                panel);
        licenseBtn.addActionListener(e -> {
            File licenseFile = new File("./res/LICENSE.html");
            LOG.info(licenseFile.toURI().getPath().trim());
            if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
                Supplement.openLink(licenseFile.toURI().getPath().replaceFirst("/", "").trim());
            } else {
                Supplement.openLink(licenseFile.toURI().getPath().trim());

            }
        });

        // Text Fields
        usernameField = Tailor.tailorTextBox(keySentinel.getUserName(), 241, 42, 33, 86, panel);
        usernameField.setFont(new Font(Font.MONOSPACED, Font.ITALIC, 16));
        passField = Tailor.tailorPassBox(keySentinel.getPassword(), 241, 42, 33, 202, panel, false);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Function not used
    }

    /**
     * OnClick function - Save
     */
    private void saveFunction() {
        // Check for change in credentials
        if (!usernameField.getText().equals(keySentinel.getUserName())
                || !String.valueOf(passField.getPassword()).equals(keySentinel.getPassword())) {
            // Check for Username Change
            if (!usernameField.getText().equals(keySentinel.getUserName())) {
                keySentinel.setUserName(usernameField.getText());
            }
            // Check for Password Change
            if (!Arrays.toString(passField.getPassword()).equals(keySentinel.getPassword())) {
                keySentinel.setPassword(String.valueOf(passField.getPassword()));
            }

            // Change the credentials
            try {
                keySentinel.setCredentials();
            } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException
                    | IOException e) {
                LOG.severe("Unable to save Credentials");
                LOG.finest(e.toString());
            }
        }
    }
}
