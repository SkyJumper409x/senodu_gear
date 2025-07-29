package xyz.skyjumper409;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URI;

import javax.swing.*;

import xyz.skyjumper409.sendougear.ImageHandler;
import xyz.skyjumper409.sendougear.data.InvalidImageSizeException;

public class App {
    JFrame frame;
    JPanel panel;
    JLabel label, resultLabel;
    JButton button;
    Font font;
    Color
        fg = Color.WHITE,
        bg = new Color(0x00102f); // Camellia - "#1f1e33 (#00102g Version)" is such a good song
    // String labelFormatString = "<html><p style=\"width:%dpx\">%s</p></html>";
    String labelFormatString = "<html><p>%s</p></html>";
    public static void main(String[] args) {
        (new App()).gui();
    }
    float fontSmall = 20, fontLarge = 30;
    private void createFont() {
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/liberationsans/LiberationMono-Bold.ttf"));
        } catch (Exception ex) {
            ex.printStackTrace();
            font = Font.getFont("Arial");
        }
    }
    private Dimension getScreenDimension() {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        return new Dimension(width, height);
    }
    private void gui() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        createFont();
        int frameWidth = 300;
        Dimension
            labelSize = new Dimension(frameWidth, 100),
            buttonSize = new Dimension(frameWidth, 250);

        int frameHeight = labelSize.height * 2 + buttonSize.height;
        Dimension sd = getScreenDimension();
        // center frame on screen
        Dimension framePos = new Dimension((sd.width / 2) - (frameWidth / 2), (sd.height / 2) - (frameHeight / 2));

        frame = new JFrame("sendou_gear");
        panel = new JPanel(new BorderLayout(0,0));
        frame.setContentPane(panel);

        label = new JLabel("");
        label.setFont(font.deriveFont(fontSmall));
        label.setMinimumSize(labelSize);
        label.setPreferredSize(labelSize);
        panel.add(label, BorderLayout.NORTH);

        resultLabel = new JLabel();
        resultLabel.setFont(font.deriveFont(fontSmall));
        resultLabel.setMinimumSize(labelSize);
        resultLabel.setPreferredSize(labelSize);
        // panel.add(resultLabel, BorderLayout.SOUTH);

        button = new JButton("Select File");
        button.setFont(font.deriveFont(fontLarge));
        button.addActionListener(new OpenActionListener());
        button.setMinimumSize(buttonSize);
        button.setPreferredSize(buttonSize);
        panel.add(button, BorderLayout.CENTER);

        frame.setForeground(fg);
        panel.setForeground(fg);
        label.setForeground(fg);
        resultLabel.setForeground(fg);
        button.setForeground(fg);

        frame.setBackground(bg);
        panel.setBackground(bg);
        label.setBackground(bg);
        resultLabel.setBackground(bg);
        button.setBackground(bg);

        frame.setMinimumSize(new Dimension(frameWidth, frameHeight));
        frame.setPreferredSize(new Dimension(frameWidth, frameHeight));
        frame.setMaximumSize(new Dimension(frameWidth * 2, frameHeight * 2));
        frame.setAlwaysOnTop(true);
        frame.setBounds(framePos.width, framePos.height, frameWidth, frameHeight);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    private void openLink(String urlString) {
        try {
            Desktop d = Desktop.getDesktop();
            if(d.isSupported(Desktop.Action.BROWSE))
                d.browse(URI.create(urlString));
            else
                Runtime.getRuntime().exec(new String[]{"xdg-open", urlString});
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    class OpenActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ev) {
            try {
                button.setEnabled(false);
                FileDialog fd = new FileDialog(frame, "Choose a file", FileDialog.LOAD);
                fd.setDirectory("/home/lynn");
                // fd.setFile("*.png");
                fd.setFilenameFilter((parent, filename) -> {
                    return filename.endsWith(".png");
                });
                fd.setVisible(true);
                fd.setMode(FileDialog.LOAD);
                String filename = fd.getFile();
                if (filename == null) {
                    showInfo("operation cancelled");
                } else {
                    System.out.println("You chose " + filename);
                    String filepath = fd.getDirectory() + filename;
                    String s = ImageHandler.calcGear(new File(filepath)).gear.toURLString();
                    resultLabel.setText(String.format(labelFormatString, s));
                    openLink(s);
                    showInfo("all good");
                }
                // JFileChooser jfc = new JFileChooser();
                // jfc.setFileFilter(new FileFilter() {
                //     @Override
                //     public boolean accept(File f) {
                //         if(f.isDirectory()) return true;
                //         String s = f.getAbsolutePath();
                //         // why are there two extensions for jpegs
                //         // also this could probably be done with like reading the first 4 bytes and checking the header,
                //         // but I'm lazy and this probably works, too.
                //         return s.endsWith(".png") || s.endsWith(".jpg") || s.endsWith(".jpeg");
                //     }
                //     @Override
                //     public String getDescription() { return "JPEGs & PNGs"; }
                // });
                // jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                // int returnState = jfc.showOpenDialog(frame);
                // System.out.println(returnState);
                // switch (returnState) {
                //     case JFileChooser.CANCEL_OPTION:
                //         showInfo("operation was cancelled");
                //         return;
                //     case JFileChooser.ERROR_OPTION:
                //         showWarn("operation errored");
                //         return;
                //     case JFileChooser.APPROVE_OPTION:
                //         File file = jfc.getSelectedFile();
                //         if(!file.exists()) {
                //             showError("file not found");
                //             return;
                //         }
                //         try {
                //             Gear g = ImageHandler.calcGear(file).gear;
                //             String s = g.toURLString();
                //             resultLabel.setText(String.format(labelFormatString, s));
                //             openLink(s);
                //         } catch (InvalidImageSizeException uoex) {
                //             if(uoex.getMessage().contains("FullHD")) {
                //                 showError(uoex.getMessage());
                //             }
                //         } catch (IOException ioex) {
                //             ioex.printStackTrace();
                //             showWarn("something went wrong (%s)", ioex.getMessage());
                //         }
                //         break;
                //     default:
                //         System.err.println("[WARN] unknown JFileChooser return value");
                //         showWarn("something went wrong");
                //         return;
                // }
                showInfo("all good");
            } catch (InvalidImageSizeException uoex) {
                showError(uoex.getMessage());
            }  catch (Exception ex) {
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
        label.setText(String.format(labelFormatString, args == null || args.length < 1 ? message : String.format(message, args)));
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
