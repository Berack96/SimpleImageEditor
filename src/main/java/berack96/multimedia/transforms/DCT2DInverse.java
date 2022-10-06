package berack96.multimedia.transforms;

import berack96.multimedia.Transform;
import berack96.multimedia.ImagesUtil;

public class DCT2DInverse implements Transform<double[][], double[][]> {
    @Override
    public double[][] transform(double[][] data) {
        final double alpha0 = ImagesUtil.getAlpha(0, data.length);
        final double alpha = ImagesUtil.getAlpha(1, data.length);
        final double[][] result = new double[data.length][data.length];

        for (int x = 0; x < data.length; x++)
            for (int y = 0; y < data.length; y++) {
                double sum = 0;
                for (int u = 0; u < data.length; u++)
                    for (int v = 0; v < data.length; v++) {
                        final double alphaMul = (u == 0 ? alpha0 : alpha) * (v == 0 ? alpha0 : alpha);
                        final double cosMul = ImagesUtil.cosDCT(x, u, data.length) * ImagesUtil.cosDCT(y, v, data.length);
                        sum += alphaMul * data[u][v] * cosMul;
                    }
                result[x][y] = sum;
            }
        return result;
    }
}
