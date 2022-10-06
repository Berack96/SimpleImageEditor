package berack96.multimedia.composting;

import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import berack96.multimedia.ImagesUtil;

public class ChromaKeying extends TwoImagesTransform {

    private final boolean useGreen;
    public ChromaKeying() { this.useGreen = false; }
    public ChromaKeying(boolean useGreen) { this.useGreen = useGreen; }

    @Override
    protected void transform(Raster foreground, Raster background, WritableRaster result) {
        if (foreground.getNumBands() != 3)
            throw new IllegalArgumentException("The two images should have RGB colors");

        final int key = useGreen ? 1 : 2;
        final int other = useGreen ? 2 : 1;
        ImagesUtil.forEachPixel(result, (width, height) -> {
            double[] pixel = foreground.getPixel(width, height, (double[]) null);
            double alpha = calcAlpha(pixel[0], pixel[other], pixel[key]);

            for (int i = 0; i < pixel.length; i++)
                pixel[i] = calcPixel(alpha, pixel[i], background.getSampleDouble(width, height, i));
            result.setPixel(width, height, pixel);
        });
    }

    static public double calcAlpha(double red, double other, double key) {
        return 1 - (key - Math.max(other, red)) / ImagesUtil.MAX_COLOR;
    }

    static public double calcPixel(double alpha, double colorF, double colorB) {
        return ImagesUtil.rangePx(alpha * colorF + (1 - alpha) * colorB);
    }
}
