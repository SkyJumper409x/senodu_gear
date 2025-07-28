package xyz.skyjumper409;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import xyz.skyjumper409.sendougear.ImageHandler;
import xyz.skyjumper409.sendougear.data.InvalidImageSizeException;

public class App {
    JFrame frame;
    JPanel panel;
    JLabel label, resultLabel;
    JButton button;
    Color
        fg = Color.WHITE,
        bg = new Color(0x00102f); // Camellia - "#1f1e33 (#00102g Version)" is such a good song
    public static void main(String[] args) {
        (new App()).gui();
    }
    private void gui() {
        frame = new JFrame("sendou_gear");
        panel = new JPanel(new BorderLayout(0,0));
        frame.setContentPane(panel);

        label = new JLabel("");
        label.setFont(label.getFont().deriveFont(20.0f));
        label.setMinimumSize(new Dimension(250, 50));
        panel.add(label, BorderLayout.NORTH);

        resultLabel = new JLabel();
        resultLabel.setMinimumSize(new Dimension(250, 50));
        panel.add(resultLabel, BorderLayout.SOUTH);

        button = new JButton("Open image file...");
        button.setFont(button.getFont().deriveFont(30.0f));
        button.addActionListener(new OpenActionListener());
        button.setMinimumSize(new Dimension(250, 250));
        panel.add(button, BorderLayout.CENTER);

        frame.setBackground(bg);
        panel.setBackground(bg);
        label.setBackground(bg);
        resultLabel.setBackground(bg);
        button.setBackground(bg);

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    class OpenActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ev) {
            try {
                button.setEnabled(false);
                JFileChooser jfc = new JFileChooser();
                jfc.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        String s = f.getAbsolutePath();
                        // why are there two extensions for jpegs
                        // also this could probably be done with like reading the first 4 bytes and checking the header,
                        // but I'm lazy and this probably works, too.
                        return s.endsWith(".png") || s.endsWith(".jpg") || s.endsWith(".jpeg");
                    }
                    @Override
                    public String getDescription() {
                        return "JPEGs & PNGs";
                    }
                });
                int returnState = jfc.showOpenDialog(frame);
                switch (returnState) {
                    case JFileChooser.CANCEL_OPTION:
                        showInfo("operation was cancelled");
                        break;
                    case JFileChooser.ERROR_OPTION:
                        showWarn("operation errored");
                        break;
                    case JFileChooser.APPROVE_OPTION:
                        File file = jfc.getSelectedFile();
                        try {
                            ImageHandler.calcGear(file);
                        } catch (InvalidImageSizeException uoex) {
                            if(uoex.getMessage().contains("FullHD")) {
                                showError("image must be 1920x1080, got");
                            }
                        } catch (IOException ioex) {
                            ioex.printStackTrace();
                            showWarn("");
                        }
                        break;
                    default:
                        System.err.println("[WARN] unknown JFileChooser return value");
                        showWarn("something went wrong");
                        break;
                }
                showInfo("all good");
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
