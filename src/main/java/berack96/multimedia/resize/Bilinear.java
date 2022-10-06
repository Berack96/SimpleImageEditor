package berack96.multimedia.resize;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import berack96.multimedia.ImagesUtil;

public class Bilinear extends Resizing {

    public Bilinear(double ratio) {
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

            int x1 = Math.min(x + 1, oldImg.getWidth() - 1);
            int y1 = Math.min(y + 1, oldImg.getHeight() - 1);

            for (int color = 0; color < newImg.getNumBands(); color++) {
                double sample = (1 - a) * (1 - b) * oldImg.getSampleDouble(x, y, color)
                        + (a) * (1 - b) * oldImg.getSampleDouble(x1, y, color)
                        + (1 - a) * (b) * oldImg.getSampleDouble(x, y1, color)
                        + (a) * (b) * oldImg.getSampleDouble(x1, y1, color);
                newImg.setSample(width, height, color, ImagesUtil.rangePx(sample));
            }
        });
        return img;
    }
}
