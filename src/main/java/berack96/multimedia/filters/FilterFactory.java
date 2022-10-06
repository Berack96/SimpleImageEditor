package berack96.multimedia.filters;

import java.util.Arrays;

public class FilterFactory {

    static public double[][] getIdentity(int size) {
        double[][] kernel = getEmptyKernel(size, 0);
        kernel[size / 2][size / 2] = 1;
        return kernel;
    }

    static public double[][] getLowPass(int size) {
        return getEmptyKernel(size, 1.0 / (size * size));
    }

    static public double[][] getHighPass(int size) {
        double[][] kernel = getEmptyKernel(size, -1.0);
        kernel[size / 2][size / 2] = size * size;
        return kernel;
    }

    static public double[][] getSharpen(int size) {
        double[][] kernel = getEmptyKernel(size, -1.0 / (size * size));
        kernel[size / 2][size / 2] = (2.0 * size * size - 1) / (size * size);
        return kernel;
    }

    static public double[][] getGaussian(int size, double sigma) {
        double[][] kernel = getEmptyKernel(size, 0);
        final int half = size / 2;
        final double div = -1.0 / (2 * sigma * sigma);
        final double divPi = 1.0 / (2 * Math.PI * sigma * sigma);

        for (int i = -half; i <= half; i++)
            for (int j = -half; j <= half; j++)
                kernel[i + half][j + half] = divPi * Math.pow(Math.E, (i * i + j * j) * div);
        return kernel;
    }

    static public double[][] getGaussianSharp(int size, double sigma) {
        return invert(getGaussian(size, sigma));
    }


    static private double[][] invert(double[][] kernel) {
        double[][] id = getIdentity(kernel.length);
        for (int i = 0; i < kernel.length; i++)
            for (int j = 0; j < kernel.length; j++)
                kernel[i][j] = 2 * id[i][j] - kernel[i][j];
        return kernel;
    }

    static private double[][] getEmptyKernel(int size, double initialVal) {
        assert size > 2 && size % 2 == 1 : "The kernel must size should be an odd number > 1";

        double[][] kernel = new double[size][size];
        for (double[] arr : kernel)
            Arrays.fill(arr, initialVal);
        return kernel;
    }
}
