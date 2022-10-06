package berack96.multimedia.transforms;

import berack96.multimedia.ImagesUtil;
import berack96.multimedia.Transform;

public class DCT1D implements Transform<double[], double[]> {
    @Override
    public double[] transform(double[] data) {
        final double[] result = new double[data.length];
        final double alpha0 = ImagesUtil.getAlpha(0, data.length);
        final double alpha = ImagesUtil.getAlpha(1, data.length);

        for (int u = 0; u < result.length; u++) {
            double sum = 0;
            for (int x = 0; x < data.length; x++)
                sum += data[x] * ImagesUtil.cosDCT(x, u, data.length);
            result[u] = (u == 0 ? alpha0 : alpha) * sum;
        }
        return result;
    }
}
