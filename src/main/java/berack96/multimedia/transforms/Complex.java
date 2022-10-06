package berack96.multimedia.transforms;

/**
 * Classe di numeri complessi usata per la trasformata di Fourier
 */
public class Complex {
    private double real = 0;
    private double imaginary = 0;


    public double getModule() {
        return Math.sqrt(real*real + imaginary*imaginary);
    }
    public double getPhase() {
        return Math.tanh(imaginary/real);
    }

    public double getReal() {
        return real;
    }

    public void setReal(double real) {
        this.real = real;
    }

    public void addReal(double real) {
        this.real += real;
    }

    public double getImaginary() {
        return imaginary;
    }

    public void setImaginary(double imaginary) {
        this.imaginary = imaginary;
    }

    public void addImaginary(double imaginary) {
        this.imaginary += imaginary;
    }
}
