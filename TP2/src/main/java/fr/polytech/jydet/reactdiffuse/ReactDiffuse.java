package fr.polytech.jydet.reactdiffuse;

import fr.polytech.jydet.utils.GifSequenceWriter;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import javax.imageio.stream.FileImageOutputStream;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ReactDiffuse extends JFrame {

    private final ReactDiffuseArguments arguments;
    private final ReactDiffuseModel model;
    private static ReactDiffuse[] reactDiffuses;
    private int size;

    public ReactDiffuse(ReactDiffuseArguments arguments) throws HeadlessException {
        super("React / Diffuse");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.arguments = arguments;
        size = 200;
        model = new ReactDiffuseModel(arguments, size);
        ReactDiffusePanel contentPane = new ReactDiffusePanel();
        contentPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tickAndSave();
            }
        });
        setContentPane(contentPane);
        int panelSize = (int) (size * 1.5);
        setSize(new Dimension(panelSize, panelSize));
        setPreferredSize(new Dimension(panelSize, panelSize));
        setVisible(true);
    }

    public void tick() {
        model.update();
        this.repaint();
    }

    private static final List<BufferedImage> images = new ArrayList<>(10);

    public static void main(String[] args) {
        final ReactDiffuseArguments arguments = new ReactDiffuseArguments();
        final CmdLineParser parser = new CmdLineParser(arguments);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException clEx) {
            System.err.println("ERROR: Unable to parse command-line options: " + clEx);
            return;
        }
        reactDiffuses = new ReactDiffuse[1];
        SwingUtilities.invokeLater(() -> reactDiffuses[0] = new ReactDiffuse(arguments));
        while (true) {
            Scanner scanner = new Scanner(System.in);
            String command = scanner.nextLine();
            if (command.equals("save")) {
                GifSequenceWriter writer;
                try(FileImageOutputStream outputStream = new FileImageOutputStream(new File("test.gif"))) {
                    writer = new GifSequenceWriter(outputStream,
                        BufferedImage.TYPE_INT_ARGB, 500, true);

                    writer.writeToSequence(images.get(0));
                    for(int i=1; i <images.size()-1; i++) {
                        writer.writeToSequence(images.get(i));
                    }

                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("saved");
            } else {
                tickAndSave();
            }
        }
    }

    private static void tickAndSave() {
//        images.add(deepCopy(reactDiffuses[0].model.getModel()));
        reactDiffuses[0].tick();
        reactDiffuses[0].repaint();
        reactDiffuses[0].model.printStats();
    }

    static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    private class ReactDiffusePanel extends JPanel {

        @Override
        public void paint(Graphics g) {
            g.drawImage(model.getModel(), 0, 0, null);
        }
    }
}
