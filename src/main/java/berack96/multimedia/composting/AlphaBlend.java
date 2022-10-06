package berack96.multimedia.composting;

import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import berack96.multimedia.ImagesUtil;

public class AlphaBlend extends TwoImagesTransform {

    private double alphaF;
    private double alphaB;

    public AlphaBlend(double alphaF, double alphaB) {
        this.alphaF = Math.max(0, Math.min(alphaF, 1));
        this.alphaB = Math.max(0, Math.min(alphaB, 1));
        this.alphaB *= 1 - this.alphaF;
    }

    @Override
    public void transform(Raster foreground, Raster background, WritableRaster result) {
        final double alphaDiv = alphaF + alphaB;
        ImagesUtil.forEachPixel(result, (width, height) -> {
            for(int color = 0; color < result.getNumBands(); color++) {
                double pixelF = foreground.getSampleDouble(width, height, color) * alphaF;
                double pixelB = background.getSampleDouble(width, height, color) * alphaB;
                double pixel = (pixelF + pixelB) / alphaDiv;
                result.setSample(width, height, color, ImagesUtil.rangePx(pixel));
            }
        });
    }
}
