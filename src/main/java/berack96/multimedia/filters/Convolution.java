package berack96.multimedia.filters;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Arrays;

import berack96.multimedia.Transform;
import berack96.multimedia.ImagesUtil;

public class Convolution implements Transform<BufferedImage, BufferedImage> {

    private final double[][] kernel;

    public Convolution(double[][] kernel) {
        this.kernel = normalize(kernel);
    }

    @Override
    public BufferedImage transform(BufferedImage source) {
        final BufferedImage result = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        final Raster src = source.getRaster();
        final WritableRaster res = result.getRaster();
        final int half = kernel.length / 2;

        ImagesUtil.forEachPixel(src, (width, height) -> {
            double[] calc = new double[src.getNumBands()];
            double[] temp = null;

            for (int y = -half; y <= half; y++) {
                int tempY = ImagesUtil.range(y + height, 0, src.getHeight() - 1);
                for (int x = -half; x <= half; x++) {
                    int tempX = ImagesUtil.range(x + width, 0, src.getWidth() - 1);

                    double kern = kernel[x + half][y + half];
                    temp = src.getPixel(tempX, tempY, temp);
                    for (int color = 0; color < calc.length; color++)
                        calc[color] += temp[color] * kern;
                }
            }
            for (int color = 0; color < calc.length; color++)
                calc[color] = ImagesUtil.rangePx(calc[color]);
            res.setPixel(width, height, calc);
        });
        return result;
    }

    static private double[][] normalize(double[][] kernel) {
        double total = 0;
        for (double[] line : kernel) {
            assert line.length == kernel.length : "The kernel must be a square";
            total += Arrays.stream(line).sum();
        }

        if (total != 1.0 && total != 0.0)
            for (double[] line : kernel)
                for (int i = 0; i < line.length; i++)
                    line[i] /= total;
        return kernel;
    }
}
