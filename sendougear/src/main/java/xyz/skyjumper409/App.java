package xyz.skyjumper409;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class App {
    JFrame frame;
    JPanel panel;
    JButton button;
    JLabel label;
    Color bg = new Color(0x00102f); // Camellia - "#1f1e33 (#00102g Version)" is such a good song
    public static void main(String[] args) {

    }
    private void gui() {
        frame = new JFrame("sendou_gear");
        panel = new JPanel(new BorderLayout(0,0));
        panel.setBackground(bg);
        frame.setContentPane(panel);

        button = new JButton("Open image file...");
        button.setFont(button.getFont().deriveFont(30.0f));
        button.addActionListener(new OpenActionListener());
        button.setMinimumSize(new Dimension(250, 250));
        panel.add(button, BorderLayout.CENTER);

        label = new JLabel("");
        label.setFont(label.getFont().deriveFont(20.0f));
        label.setMinimumSize(new Dimension(250, 50));
        panel.add(label, BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
    }
    class OpenActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ev) {
            try {
                button.setEnabled(false);
                JFileChooser jfc = new JFileChooser();
                int returnState = jfc.showOpenDialog(frame);
                switch (returnState) {
                    case JFileChooser.CANCEL_OPTION:

                        break;
                    case JFileChooser.ERROR_OPTION:

                        break;
                    case JFileChooser.APPROVE_OPTION:

                        break;
                    default:
                        System.err.println("[WARN] unknown JFileChooser return value");
                        break;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                button.setEnabled(true);
            }
        }
    }
    public static final int INFO = 0, WARN = 1, ERROR = 2;
    Color[] logCols = new Color[]{Color.WHITE, Color.YELLOW, Color.RED};
    void labelThing(int level, String message, Object... args) {
        label.setForeground(logCols[level]);
        label.setText(args == null || args.length < 1 ? message : String.format(message, args));
    }
    void labelThing(int level, String message) {
        labelThing(level, message, (Object[])null);
    }
    void showInfo(String message, Object... args) { labelThing(INFO, message, args); }
    void showInfo(String message) { showInfo(message, (Object[])null); }
    void showWarn(String message, Object... args) { labelThing(WARN, message, args); }
    void showWarn(String message) { showWarn(message, (Object[])null); }
    void showError(String message, Object... args) { labelThing(ERROR, message, args); }
    void showError(String message) { showError(message, (Object[])null); }
}
