package berack96.multimedia.transforms;

import berack96.multimedia.Transform;

public class DFTInverse implements Transform<Complex[], double[]> {
    @Override
    public double[] transform(Complex[] data) {
        final double[] result = new double[data.length];

        for (int k = 0; k < result.length; k++)
            for (int x = 0; x < data.length; x++) {
                double angle = (2 * Math.PI * k * x) / data.length;
                result[k] += data[x].getReal() * Math.cos(angle) - data[x].getImaginary() * Math.sin(angle);
            }
        return result;
    }
}
