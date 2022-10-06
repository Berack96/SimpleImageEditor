package berack96.multimedia.resize;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import berack96.multimedia.ImagesUtil;

public class Bicubic extends Resizing {

    public Bicubic(double ratio) {
        super(ratio, ratio);
    }

    @Override
    public BufferedImage transform(BufferedImage obj) {
        BufferedImage img = createResized(obj);
        Raster oldImg = obj.getRaster();
        WritableRaster newImg = img.getRaster();

        ImagesUtil.forEachPixel(newImg, (width, height) -> {
            int x = (int) (width / ratioWidth);
            int y = (int) (height / ratioHeight);
            double a = (width / ratioWidth) - x;
            double b = (height / ratioHeight) - y;

            // Pre calc all values
            int[] x1 = new int[SIZE], y1 = new int[SIZE];
            double[] pa = new double[SIZE], pb = new double[SIZE];
            for (int k = 0; k < SIZE; k++) {
                x1[k] = ImagesUtil.range(x + k - 1, 0, oldImg.getWidth() - 1);
                y1[k] = ImagesUtil.range(y + k - 1, 0, oldImg.getHeight() - 1);
                pa[k] = phi(k - 1, a);
                pb[k] = phi(k - 1, b);
            }

            for (int color = 0; color < oldImg.getNumBands(); color++) {
                double sample = 0;
                for (int k = 0; k < SIZE; k++)
                    for (int l = 0; l < SIZE; l++)
                        sample += oldImg.getSampleDouble(x1[k], y1[l], color) * pa[k] * pb[l];
                newImg.setSample(width, height, color, ImagesUtil.rangePx(sample));
            }
        });
        return img;
    }

    private static final int SIZE = 4;
    private static double phi(int index, double h) {
        double h2 = h * h;
        double h3 = h2 * h;
        return switch (index) {
            case -1 -> (-h3 + (3 * h2) - (2 * h)) / 6;
            case 0 -> (h3 - (2 * h2) - h + 2) / 2;
            case 1 -> (-h3 + h2 + (2 * h)) / 2;
            case 2 -> (h3 - h) / 6;
            default -> 0;
        };
    }
}
