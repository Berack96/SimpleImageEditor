package berack96.test.multimedia;

import org.junit.jupiter.api.Test;

import berack96.multimedia.transforms.*;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class MultimediaTest {
    final static double DECIMAL_ERR = 0.0000000001;

    @Test
    public void testDCT1D() {
        DCT1D dct = new DCT1D();
        DCT1DInverse inv = new DCT1DInverse();
        double[] src = new double[]{20, 12, 18, 56, 83, 110, 104, 115};
        double[] trs = dct.transform(src);
        assertArrayEquals(new double[]{183.1, -113.0, -4.1, 22.1, 10.6, -1.5, 4.8, -8.7}, trs, 0.05); // round error
        double[] rev = inv.transform(trs);
        assertArrayEquals(src, rev, DECIMAL_ERR);

        src = new double[]{15, 186, 15, 498, 85, 45, 864, 846, 468, 7564, 4, 0, 39, 57, 84, 19};
        trs = inv.transform(dct.transform(src));
        assertArrayEquals(src, trs, DECIMAL_ERR);

        src = createRandom(1000);
        trs = inv.transform(dct.transform(src));
        assertArrayEquals(src, trs, DECIMAL_ERR);
    }

    @Test
    public void testDCT2D() {
        DCT2D dct = new DCT2D();
        DCT2DInverse inv = new DCT2DInverse();
        double[][] mrx = new double[][]{{15.62214, 45.6}, {30.36, 51.014}};
        double[][] res = inv.transform(dct.transform(mrx));
        for (int i = 0; i < res.length; i++)
            assertArrayEquals(mrx[i], res[i], DECIMAL_ERR);

        int lenRand = 32;
        mrx = new double[lenRand][];
        for (int i = 0; i < mrx.length; i++)
            mrx[i] = createRandom(lenRand);
        res = inv.transform(dct.transform(mrx));
        for (int i = 0; i < res.length; i++)
            assertArrayEquals(mrx[i], res[i], DECIMAL_ERR);
    }

    @Test
    public void testDFT() {
        DFT dft = new DFT();
        DFTInverse inv = new DFTInverse();
        double[] data = new double[]{15.62214, 45.6, 94.63, 10.85, 2.85};
        double[] res = inv.transform(dft.transform(data));
        assertArrayEquals(data, res, DECIMAL_ERR);

        data = createRandom(1000);
        res = inv.transform(dft.transform(data));
        assertArrayEquals(data, res, DECIMAL_ERR);
    }

    static private double[] createRandom(int length) {
        double[] data = new double[length];
        for (int i = 0; i < data.length; i++)
            data[i] = (Math.random() - 0.2) * 100;
        return data;
    }
}
