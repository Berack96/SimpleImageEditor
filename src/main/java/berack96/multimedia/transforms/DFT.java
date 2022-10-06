package berack96.multimedia.transforms;

import berack96.multimedia.Transform;

public class DFT implements Transform<double[], Complex[]> {

    @Override
    public Complex[] transform(double[] data) {
        final Complex[] result = new Complex[data.length];

        for (int k = 0; k < result.length; k++) {
            Complex num = new Complex();
            for (int x = 0; x < data.length; x++) {
                double angle = (2 * Math.PI * k * x) / data.length;
                num.addReal(data[x] * Math.cos(angle));
                num.addImaginary(data[x] * Math.sin(angle));
            }
            num.setReal(num.getReal() / data.length);
            num.setImaginary(-num.getImaginary() / data.length);

            result[k] = num;
        }

        return result;
    }
}
