package berack96.multimedia.view;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import berack96.multimedia.ImagesUtil;
import berack96.multimedia.composting.AlphaBlend;
import berack96.multimedia.composting.ChromaKeying;
import berack96.multimedia.composting.ChromaKeying3D;
import berack96.multimedia.compression.JPEG;
import berack96.multimedia.filters.Convolution;
import berack96.multimedia.filters.FilterFactory;
import berack96.multimedia.resize.Bicubic;
import berack96.multimedia.resize.Bilinear;
import berack96.multimedia.resize.NearestNeighbor;

import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;

public class Main {
    final static String PATH = "src/resources/sample/";

    public static void main(String[] args) throws Exception {
        ImagesUtil.maxThreads = 4;
        runEditor();
    }

    static private JFrame runEditor() {
        var imageFrame = new JFrame("Simple Image Editor") { public BufferedImage image; public JLabel label;};
        
        // All the menu options
        var utility = new JMenu("File");
        utility.add(menuItem("Save", () -> showChooser(false, imageFrame.image)));
        utility.add(menuItem("Load", () -> showChooser(true, imageFrame.image)));
        
        var compose = new JMenu("Compose");
        compose.add(menuItem("Alpha Blend", () -> new AlphaBlend(0.2, 0.8).transform(imageFrame.image, showChooser(true, imageFrame.image, true))));
        compose.add(menuItem("Chroma Keying", () -> new ChromaKeying(true).transform(imageFrame.image, showChooser(true, imageFrame.image, true))));
        compose.add(menuItem("Chroma Keying 3D", () -> new ChromaKeying3D(180, 120, 0, 255, 0).transform(imageFrame.image, showChooser(true, imageFrame.image, true))));
        
        var compress = new JMenu("Compression");
        compress.add(menuItem("JPEG", () -> new JPEG().process(imageFrame.image, 1)));

        var filters = new JMenu("Filters");
        var kernelSize = 5;
        filters.add(menuItem("Blur", () -> new Convolution(FilterFactory.getLowPass(kernelSize)).transform(imageFrame.image)));
        filters.add(menuItem("Sharpen", () -> new Convolution(FilterFactory.getSharpen(kernelSize)).transform(imageFrame.image)));
        filters.add(menuItem("Ridge Detection", () -> new Convolution(FilterFactory.getHighPass(kernelSize)).transform(imageFrame.image)));
        filters.add(menuItem("Gaussian Blur", () -> new Convolution(FilterFactory.getGaussian(kernelSize, 1)).transform(imageFrame.image)));
        filters.add(menuItem("Gaussian Sharpen", () -> new Convolution(FilterFactory.getGaussianSharp(kernelSize, 1)).transform(imageFrame.image)));

        var resizes = new JMenu("Resizes");
        var ratio = new float[]{1.5f, 0.75f};
        for(int i=0; i<ratio.length; i++) {
            final var ratioVal = ratio[i];
            final var ratioStr = String.format("x%1.2f", ratioVal);
            resizes.add(menuItem(ratioStr + " NearestNeighbor", () -> new NearestNeighbor(ratioVal).transform(imageFrame.image)));
            resizes.add(menuItem(ratioStr + " Bilinear", () -> new Bilinear(ratioVal).transform(imageFrame.image)));
            resizes.add(menuItem(ratioStr + " Bicubic", () -> new Bicubic(ratioVal).transform(imageFrame.image)));
        }

        var menuBar = new JMenuBar();
        menuBar.add(utility);
        menuBar.add(compress);
        menuBar.add(compose);
        menuBar.add(filters);
        menuBar.add(resizes);

        // The frame initialization
        var dim = Toolkit.getDefaultToolkit().getScreenSize();
        imageFrame.label = new JLabel();
        imageFrame.add(new JScrollPane(imageFrame.label));
        imageFrame.setJMenuBar(menuBar);
        imageFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        imageFrame.setSize(dim.width / 2, dim.height / 2);
        imageFrame.setLocationRelativeTo(null);
        imageFrame.setResizable(true);
        imageFrame.setVisible(true);
        
        return imageFrame;
    }

    static private BufferedImage showChooser(boolean open, BufferedImage image) {
        return showChooser(open, image, false);
    }
    static private BufferedImage showChooser(boolean open, BufferedImage image, boolean sameSize) {
        var chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));
        chooser.setFileFilter(new FileNameExtensionFilter("PNG, JPEG", "png", "jpg", "jpeg"));
        try {
            if(open && chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                var temp = ImageIO.read(chooser.getSelectedFile());
                if(sameSize && image != null && temp != null && (image.getWidth() != temp.getWidth() || image.getHeight() != temp.getHeight()))
                    temp = new Bicubic(1).setRatio((double) image.getWidth() / temp.getWidth(), (double) image.getHeight() / temp.getHeight()).transform(temp);
                image = temp;
            }
            if(!open && chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                var file = chooser.getSelectedFile();
                ImageIO.write(image, "png", file);
                if(!file.getName().endsWith(".png"))
                    file.renameTo(new File(file.getPath() + ".png"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    static private JMenuItem menuItem(String name, Supplier<BufferedImage> supplier) {
        var item = new JMenuItem(name);
        item.addActionListener((event) -> {
            try {
                var image = supplier.get();
                if(image == null) return;

                var popup = (JPopupMenu) item.getParent();
                var frame = (JFrame) SwingUtilities.getRoot(popup.getInvoker());
                
                var fieldIco = frame.getClass().getField("label");
                ((JLabel) fieldIco.get(frame)).setIcon(new ImageIcon(image));
                
                var fieldImg = frame.getClass().getField("image");
                fieldImg.set(frame, image);

                frame.pack();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return item;
    }
}
