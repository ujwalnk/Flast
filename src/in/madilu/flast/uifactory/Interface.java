package in.madilu.flast.uifactory;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Insets;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Logger;

import javax.crypto.NoSuchPaddingException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import in.madilu.flast.sentinel.NetSentinel;
import in.madilu.flast.sentinel.Supplement;

public class Interface extends JFrame implements ActionListener {
    private static final JPanel PANEL = new JPanel();
    private static final CardLayout CARD_LAYOUT = new CardLayout();

    private static final JPanel MAIN_PANEL = new JPanel();

    private static final String MAIN_PANEL_STRING = "MAIN PANEL";
    private static final String OTHER_PANEL_STRING = "OTHER PANEL";

    private static JPanel otherPanel = new JPanel();

    public static Insets windowInsets;

    private static final Logger LOG = Supplement.readyLogger(Logger.getLogger("in.Madilu.Flast.UIFactory.Interface"));

    static {
        LOG.info(() -> String.format("System OS: %s", System.getProperty("os.name")));
    }

    public Interface() throws IOException {
        // Setting Window Parameters
        setTitle("Flast");
        setBounds(300, 300, 610 + getInsets().left + getInsets().right, 410 + getInsets().top);
        getContentPane().setBackground(Color.white);

        // Set App Icon
        ImageIcon icon = new ImageIcon("./res/icon.png");
        setIconImage(icon.getImage());

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        PANEL.setPreferredSize(new Dimension(610, 410));
        PANEL.setLayout(CARD_LAYOUT);
        windowInsets = getInsets();

        PaintMain mainCard = new PaintMain(MAIN_PANEL);
        MAIN_PANEL.setLayout(null);

        PANEL.add(MAIN_PANEL_STRING, MAIN_PANEL);
        PANEL.add(OTHER_PANEL_STRING, otherPanel);

        add(PANEL);
        setVisible(true);

        showHome();
        windowInsets = getInsets();

        // Disconnect on window Close and Exit
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mainCard.changeStatusText("Please Wait. Disconnecting");
                try {
                    if (NetSentinel.disconnect()) {
                        LOG.info("Logout & Close");
                    } else {
                        LOG.warning("Unable to Logout at Close");
                    }
                } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException
                        | NoSuchPaddingException | IOException e1) {
                    LOG.warning("Unable to disconnect");
                    LOG.finest(e1.toString());
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Method Not Working
    }

    /**
     * Display Home Panel
     */
    public static void showHome() {
        CARD_LAYOUT.show(PANEL, MAIN_PANEL_STRING);
    }

    /**
     * Display Config Panel
     */
    public static void showConfig() {
        new PaintConfig(otherPanel);
        CARD_LAYOUT.show(PANEL, OTHER_PANEL_STRING);
    }
}
