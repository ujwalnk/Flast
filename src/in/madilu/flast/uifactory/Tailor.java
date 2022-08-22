package in.madilu.flast.uifactory;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.MouseInputAdapter;

public class Tailor {
    private Tailor() {
        // Empty Constructor
    }

    /**
     * Create a styled Jbutton
     * 
     * @param text    - Button Text
     * @param bgColor - Background Color
     * @param width   - Button width
     * @param height  - Button height
     * @param x       - Button x location
     * @param y       - Button y location
     * @return - JButton
     */
    public static JButton tailorButton(String text, String bgColor, int width, int height, int x, int y, Container c) {

        JButton button = new JButton(text);

        // Set the foreground and background colors of the button
        button.setForeground(Color.decode("#FFFFFFF"));
        button.setBackground(Color.decode(bgColor));

        // Set border color to transparent
        Border line = new LineBorder(Color.decode("#00FFFFFF"));

        // Set margin and compound
        Border margin = new EmptyBorder(5, 15, 5, 15);
        Border compound = new CompoundBorder(line, margin);

        // Set the properties
        button.setBorder(compound);

        // Set font properties of button
        button.setFont(new Font(Font.DIALOG, Font.PLAIN, 16));

        // Set button location and size
        button.setSize(width, height);
        button.setLocation(x - Interface.windowInsets.left, y - Interface.windowInsets.top);

        // Change cursor to Hand
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Remove focus Outline
        button.setFocusPainted(false);

        button.addMouseListener(new MouseInputAdapter() {

            // Hover Color Changes
            @Override
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(Color.decode(bgColor).darker());
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                button.setBackground(Color.decode(bgColor));
            }

            // Click Size Change
            @Override
            public void mouseClicked(MouseEvent evt) {
                button.setBackground(Color.decode(bgColor).darker().darker());
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                button.setBackground(Color.decode(bgColor));
                            }
                        },
                        200);
            }
        });

        // Add and return button Object
        c.add(button);
        return button;
    }

    public static JTextField tailorTextBox(String text, int width, int height, int x, int y, Container c) {

        JTextField textField = new JTextField(text);

        // Set font
        textField.setFont(new Font(Font.DIALOG, Font.PLAIN, 16));

        // Set Colors and Border
        textField.setBackground(Color.decode("#E9E9E9"));
        textField.setForeground(Color.BLACK);
        textField.setColumns(30);

        // Set border color to transparent
        Border line = new LineBorder(Color.gray.darker());

        // Set margin and compound
        Border margin = new EmptyBorder(5, 15, 5, 15);
        Border compound = new CompoundBorder(line, margin);

        // Set the properties
        textField.setBorder(compound);

        // Set dimensions
        textField.setSize(width, height);

        // Add to Container
        textField.setLocation(x - Interface.windowInsets.left, y - Interface.windowInsets.top);
        c.add(textField);

        return textField;
    }

    public static JPasswordField tailorPassBox(String text, int width, int height, int x, int y, Container c,
            boolean showPassword) {

        JPasswordField passField = new JPasswordField(text);

        // Set font
        passField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));

        // Set Colors and Border
        passField.setBackground(Color.decode("#E9E9E9"));
        passField.setForeground(Color.BLACK);
        passField.setColumns(30);

        // Set border color to transparent
        Border line = new LineBorder(Color.gray.darker());

        // Set margin and compound
        Border margin = new EmptyBorder(5, 15, 5, 15);
        Border compound = new CompoundBorder(line, margin);

        // Set the properties
        passField.setBorder(compound);

        // Set dimensions
        passField.setSize(width, height);

        // Add to Container
        passField.setLocation(x - Interface.windowInsets.left, y - Interface.windowInsets.top);
        c.add(passField);

        // Hide password
        if (showPassword) {
            passField.setEchoChar((char) 0);
        } else {
            passField.setEchoChar('⚫');
        }

        return passField;
    }

    public static JLabel tailorLabel(String text, int width, int height, int x, int y, Container c) {
        JLabel label = new JLabel(text);

        // Set Color and Font
        label.setBackground(Color.WHITE);
        label.setForeground(Color.BLACK);
        label.setFont(new Font(Font.DIALOG, Font.PLAIN, 16));

        // Set position
        label.setBounds(x - Interface.windowInsets.left, y - Interface.windowInsets.top, width, height);

        c.add(label);
        return label;
    }

    public static JToggleButton tailorTglButton(String[] states, String bgColor, int width, int height,
            int x, int y, Container c) {
        JToggleButton button = new JToggleButton(states[0]);

        // Set the foreground and background colors of the button
        button.setForeground(Color.decode("#FFFFFFF"));
        button.setBackground(Color.decode(bgColor));

        // Set border color to transparent
        Border line = new LineBorder(Color.decode("#00FFFFFF"));

        // Set margin and compound
        Border margin = new EmptyBorder(5, 15, 5, 15);
        Border compound = new CompoundBorder(line, margin);

        // Set the properties
        button.setBorder(compound);

        // Set font properties of button
        button.setFont(new Font(Font.DIALOG, Font.ITALIC, 16));

        // Set button location and size
        button.setSize(width, height);
        button.setLocation(x - Interface.windowInsets.left - c.getInsets().right,
                y - Interface.windowInsets.top - c.getInsets().bottom);

        // Change cursor to Hand
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Remove focus Outline
        button.setFocusPainted(false);

        button.addMouseListener(new MouseInputAdapter() {

            // Hover Color Changes
            @Override
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(Color.decode(bgColor).darker());
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                button.setBackground(Color.decode(bgColor));
            }

            // Click Size Change
            @Override
            public void mouseClicked(MouseEvent evt) {
                button.setLocation(x - Interface.windowInsets.left - c.getInsets().right + 2,
                        y - Interface.windowInsets.top - c.getInsets().bottom + 2);
                button.setSize(width - 4, height - 4);
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                button.setSize(width, height);
                                button.setLocation(x - Interface.windowInsets.left - c.getInsets().right,
                                        y - Interface.windowInsets.top - c.getInsets().bottom);
                            }
                        },
                        200);
            }
        });

        button.addItemListener(e -> {
            int state = e.getStateChange();

            // Change the state of the button
            if (state == ItemEvent.SELECTED) {
                button.setText(states[1]);
            } else {
                button.setText(states[0]);
            }
        });

        // Add and return button Object
        c.add(button);
        return button;
    }
}