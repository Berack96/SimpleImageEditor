package berack96.multimedia.transforms;

import berack96.multimedia.Transform;
import berack96.multimedia.ImagesUtil;

public class DCT1DInverse implements Transform<double[], double[]> {
    @Override
    public double[] transform(double[] data) {
        final double[] result = new double[data.length];
        final double alpha0 = ImagesUtil.getAlpha(0, data.length);
        final double alpha = ImagesUtil.getAlpha(1, data.length);

        for (int i = 0; i < result.length; i++)
            for (int x = 0; x < data.length; x++)
                result[i] += (x == 0 ? alpha0 : alpha) * data[x] * ImagesUtil.cosDCT(i, x, data.length);
        return result;
    }
}
