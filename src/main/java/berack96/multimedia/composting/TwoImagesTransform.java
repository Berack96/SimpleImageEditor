package berack96.multimedia.composting;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import berack96.multimedia.Transform;

public abstract class TwoImagesTransform implements Transform<BufferedImage[], BufferedImage> {
    @Override
    public BufferedImage transform(BufferedImage...obj) {
        if(obj == null || obj.length != 2)
            throw new IllegalArgumentException("Need exactly 2 images");

        final Raster foreground = obj[0].getRaster();
        final Raster background = obj[1].getRaster();

        if(foreground.getHeight() != background.getHeight())
            throw new IllegalArgumentException("The 2 images must be equal in height");
        if(foreground.getWidth() != background.getWidth())
            throw new IllegalArgumentException("The 2 images must be equal in width");
        if(obj[0].getType() != obj[1].getType())
            throw new IllegalArgumentException("The 2 images must have the same color type");

        BufferedImage result = new BufferedImage(obj[0].getWidth(), obj[0].getHeight(), obj[0].getType());
        transform(foreground, background, result.getRaster());
        return result;
    }

    protected abstract void transform(Raster foreground, Raster background, WritableRaster result);
}
