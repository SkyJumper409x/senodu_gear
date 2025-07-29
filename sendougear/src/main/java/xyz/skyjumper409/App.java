package xyz.skyjumper409;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.File;
import java.net.URI;

import javax.swing.*;

import xyz.skyjumper409.sendougear.IOStuff;
import xyz.skyjumper409.sendougear.ImageHandler;
import xyz.skyjumper409.sendougear.data.Ability;
import xyz.skyjumper409.sendougear.data.Gear;
import xyz.skyjumper409.sendougear.data.InvalidImageSizeException;

public class App {
    JFrame frame;
    JPanel panel;
    JLabel label;
    JButton button;
    Font font;
    Color
        fg = Color.WHITE,
        bg = new Color(0x00102f); // Camellia - "#1f1e33 (#00102g Version)" is such a good song
    // String labelFormatString = "<html><p style=\"width:%dpx\">%s</p></html>";
    static final String labelFormatString = "<html><p>%s</p></html>";
    Gear resultGear = null;
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
        int frameWidth = 250;
        Dimension
            labelSize = new Dimension(frameWidth, Math.max(100, ImagePanel.bgh * 3)),
            buttonSize = new Dimension(frameWidth, 200);

        int frameHeight = labelSize.height + buttonSize.height;
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
        ImagePanel labelPanel = new ImagePanel();
        labelPanel.add(label);
        panel.add(labelPanel, BorderLayout.NORTH);

        button = new JButton("Select File");
        button.setFont(font.deriveFont(fontLarge));
        button.addActionListener(new OpenActionListener());
        button.setMinimumSize(buttonSize);
        button.setPreferredSize(buttonSize);
        panel.add(button, BorderLayout.CENTER);

        frame.setForeground(fg);
        panel.setForeground(fg);
        label.setForeground(fg);
        button.setForeground(fg);

        frame.setBackground(bg);
        panel.setBackground(bg);
        label.setBackground(bg);
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
                resultGear = null;
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
                    Logger.debug("You chose " + filename);
                    String filepath = fd.getDirectory() + filename;
                    resultGear = ImageHandler.calcGear(new File(filepath)).gear;
                    String s = resultGear.toURLString();
                    openLink(s);
                    if(ImagePanel.canDisplayGear)
                        showInfo("");
                    else
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
                // showInfo("all good");
            } catch (InvalidImageSizeException uoex) {
                showError(uoex.getMessage());
            }  catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                button.setEnabled(true);
            }
        }
    }
    final Color abilityDisplayBg = new Color(0xa1a1a1);
    // from https://docs.oracle.com/javase/tutorial/2d/advanced/examples/Composite.java
    class ImagePanel extends JPanel {
        static final BufferedImage bgMain, bgSub;
        static final int bgmw, bgsw, bgh;
        static final boolean canDisplayGear;
        private static AffineTransform[][] abilityBgTransforms = new AffineTransform[3][4];
        private static AffineTransform[][] abilityTransforms = new AffineTransform[3][4];
        private static final double mainScale = 43.0 / 126.0, subScale = 34.0 / 126.0;
        static {
            BufferedImage bgm = null, bgs = null;
            boolean cdg = false;
            try {
                bgm = IOStuff.readResourceImage("/img/resting_bg_for_display_main.png");
                bgs = IOStuff.readResourceImage("/img/resting_bg_for_display_sub.png");
                cdg = true;
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                bgMain = bgm;
                bgSub = bgs;
                canDisplayGear = cdg;
                if(cdg) {
                    bgmw = bgm.getWidth();
                    bgsw = bgs.getWidth();
                    bgh = bgm.getHeight();
                } else {
                    bgmw = 0;
                    bgsw = 0;
                    bgh = 0;
                }
                if(cdg)
                for (int i = 0; i < 3; i++) {
                    int x = 0;
                    int y = bgh * i;
                    abilityBgTransforms[i][0] = new AffineTransform(1, 0, 0, 1, x,     y    );
                    abilityTransforms[i][0]   = new AffineTransform(1, 0, 0, 1, x + 1, y + 2);
                    abilityTransforms[i][0].scale(mainScale, mainScale);
                    for (int j = 1; j < 4; j++) {
                        x = bgmw + bgsw * (j - 1);
                        abilityBgTransforms[i][j] = new AffineTransform(1, 0, 0, 1, x,     y    );
                        abilityTransforms[i][j]   = new AffineTransform(1, 0, 0, 1, x + 3, y + 6);
                        abilityTransforms[i][j].scale(subScale, subScale);
                    }
                }
            }
        }
        public ImagePanel(){}

        @Override
        public Dimension getMinimumSize() {
            return new Dimension();
        }
        public void paintComponent(Graphics g) {
            super.paintComponent( g );
            Graphics2D g2 = (Graphics2D) g;


            Dimension d = getSize();
            // Clears the previously drawn image.
            g2.setColor(abilityDisplayBg);
            g2.fillRect(0, 0, d.width, d.height);
            if(canDisplayGear) {
                BufferedImage buffImg = gearImage();
                if(buffImg != null) {
                    // Draws the buffered image.
                    g2.drawImage(buffImg, null, (getWidth() - buffImg.getWidth()) / 2, 4);
                }
            }
        }
        public BufferedImage gearImage() {
            if(resultGear == null)
                return null;
            BufferedImage gearImage = new BufferedImage(bgmw + (bgsw * 3), bgh * 3, BufferedImage.TYPE_INT_ARGB);
            Graphics2D gbi = gearImage.createGraphics();
            for (int i = 0; i < 3; i++) {
                Ability[] abilities = resultGear.get(i).abilities;
                for (int j = 0; j < 4; j++) {
                    gbi.drawRenderedImage(j == 0 ? bgMain : bgSub, abilityBgTransforms[i][j]);
                    Ability a = abilities[j];
                    if(a != null)
                        gbi.drawRenderedImage(a.image, abilityTransforms[i][j]);
                }
            }
            return gearImage;
        }
        public BufferedImage exampleImage() {
            Dimension d = getSize();
            int w = d.width;
            int h = d.height;
            // Creates the buffered image.
            BufferedImage buffImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D gbi = buffImg.createGraphics();


            int rectx = w/4;
            int recty = h/4;

            // Draws the rectangle and ellipse into the buffered image.
            gbi.setColor(new Color(0.0f, 0.0f, 1.0f, 1.0f));
            gbi.fill(new Rectangle2D.Double(rectx, recty, 150, 100));
            gbi.setColor(new Color(1.0f, 0.0f, 0.0f, 1.0f));
            gbi.setComposite(AlphaComposite.SrcOver);
            gbi.fill(new Ellipse2D.Double(rectx+rectx/2,recty+recty/2,150,100));
            return buffImg;
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
