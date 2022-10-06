package berack96.multimedia.transforms;

import berack96.multimedia.Transform;
import berack96.multimedia.ImagesUtil;

public class DCT2D implements Transform<double[][], double[][]> {
    @Override
    public double[][] transform(double[][] data) {
        final double alpha0 = ImagesUtil.getAlpha(0, data.length);
        final double alpha = ImagesUtil.getAlpha(1, data.length);
        double[][] result = new double[data.length][data.length];

        for (int u = 0; u < data.length; u++)
            for (int v = 0; v < data.length; v++) {
                double sum = 0;
                for (int x = 0; x < data.length; x++)
                    for (int y = 0; y < data.length; y++)
                        sum += data[x][y] * ImagesUtil.cosDCT(x, u, data.length) * ImagesUtil.cosDCT(y, v, data.length);
                result[u][v] = (u == 0 ? alpha0 : alpha) * (v == 0 ? alpha0 : alpha) * sum;
            }
        return result;
    }
}
