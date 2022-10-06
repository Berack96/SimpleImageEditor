package berack96.multimedia.resize;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import berack96.multimedia.ImagesUtil;

public class NearestNeighbor extends Resizing {

    public NearestNeighbor(double ratio) {
        super(ratio, ratio);
    }

    @Override
    public BufferedImage transform(BufferedImage obj) {
        BufferedImage img = createResized(obj);
        Raster oldImg = obj.getRaster();
        WritableRaster newImg = img.getRaster();

        ImagesUtil.forEachPixel(newImg, (width, height) -> {
            for (int color = 0; color < newImg.getNumBands(); color++)
                newImg.setSample(width, height, color, oldImg.getSampleDouble((int) (width / ratioWidth), (int) (height / ratioHeight), color));
        });
        return img;
    }
}
