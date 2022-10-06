package berack96.multimedia.composting;

import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import berack96.multimedia.ImagesUtil;

public class ChromaKeying3D extends ChromaKeying {
    final private double[] keyColor = {0, 255, 0};
    private double keySphere;
    private double keyTolerance;

    public ChromaKeying3D(double radius, double tolerance) {
        this(radius, tolerance, 0, 255, 0);
    }
    public ChromaKeying3D(double radius, double tolerance, double red, double green, double blue) {
        super();
        this.keySphere = radius < 1 ? 1 : radius * radius;
        this.keyTolerance = tolerance < 1 ? 1 : tolerance * tolerance;
        keyColor[0] = ImagesUtil.rangePx(red);
        keyColor[1] = ImagesUtil.rangePx(green);
        keyColor[2] = ImagesUtil.rangePx(blue);
    }

    @Override
    protected void transform(Raster foreground, Raster background, WritableRaster result) {
        if (foreground.getNumBands() != 3)
            throw new IllegalArgumentException("The two images should have RGB colors");

        ImagesUtil.forEachPixel(result, (width, height) -> {
            double[] pixel = foreground.getPixel(width, height, (double[]) null);
            double x = pixel[0] - keyColor[0];
            double y = pixel[1] - keyColor[1];
            double z = pixel[2] - keyColor[2];
            double point = x * x + y * y + z * z;
            double alpha = (point - keySphere) / keyTolerance;
            alpha = Math.max(0, Math.min(alpha, 1));

            for (int i = 0; i < pixel.length; i++)
                pixel[i] = calcPixel(alpha, pixel[i], background.getSampleDouble(width, height, i));
            result.setPixel(width, height, pixel);
        });
    }
}
