package berack96.multimedia.compression;

import javax.imageio.ImageIO;

import berack96.multimedia.ImagesUtil;
import berack96.multimedia.transforms.DCT2D;
import berack96.multimedia.transforms.DCT2DInverse;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

/**
 * Classe che converte una immagine nella sua rappresentazione JPEG
 */
public class JPEG {
    static public final int BLOCK_SIZE = 8;
    static public final int NORMALIZER = ImagesUtil.MAX_COLOR / 2 + 1;
    static private final int[][] QT_LUMINANCE = {
            {16, 11, 10, 16, 24, 40, 51, 61},
            {12, 12, 14, 19, 26, 58, 60, 55},
            {14, 13, 16, 24, 40, 57, 69, 56},
            {14, 17, 22, 29, 51, 87, 80, 62},
            {18, 22, 37, 56, 68, 109, 103, 77},
            {24, 35, 55, 64, 81, 104, 113, 92},
            {49, 64, 78, 87, 103, 121, 120, 101},
            {72, 92, 95, 98, 112, 100, 103, 99}
    };
    static private final int[][] QT_CHROMINANCE = {
            {17, 18, 24, 47, 99, 99, 99, 99},
            {18, 21, 26, 66, 99, 99, 99, 99},
            {24, 26, 56, 99, 99, 99, 99, 99},
            {47, 66, 99, 99, 99, 99, 99, 99},
            {99, 99, 99, 99, 99, 99, 99, 99},
            {99, 99, 99, 99, 99, 99, 99, 99},
            {99, 99, 99, 99, 99, 99, 99, 99},
            {99, 99, 99, 99, 99, 99, 99, 99}
    };


    /**
     * Processa una immagine nella sua rappresentazione JPEG
     * @param imageFile la path del file da modificare
     * @param factor il fattore moltiplicativo per la quantizzazione
     * @return l'immagine modificata dalla quantizzazione JPEG
     * @throws IOException nel caso l'accesso al file fallisca
     */
    public BufferedImage process(String imageFile, double factor) throws IOException {
        return process(ImageIO.read(new File(imageFile)), factor);
    }

    /**
     * Processa una immagine nella sua rappresentazione JPEG
     * @param image l'immagine da modificare
     * @param factor il fattore moltiplicativo per la quantizzazione
     * @return l'immagine modificata dalla quantizzazione JPEG
     */
    public BufferedImage process(BufferedImage image, double factor) {
        final BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        final WritableRaster raster = img.getRaster();
        image.copyData(raster);

        ImagesUtil.forEachPixel(raster, BLOCK_SIZE, (x, y) -> processBlock(raster, x, y, factor));
        return img;
    }

    /**
     * Processa un singolo blocco di un raster e lo modifica dopo avergli applicato:<br>
     * - discrete cosine transform 2D
     * - quantizzazione
     * - dequantizzazione
     * - discrete cosine transform 2D inverse
     * 
     * @param img il raster a cui applicare l'algoritmo
     * @param width il pixel in alto a sinistra del blocco da processare
     * @param height il pixel in alto a sinistra del blocco da processare
     * @param factor il fattore moltiplicativo della quantizzazione
     */
    static public void processBlock(WritableRaster img, int width, int height, double factor) {
        writeBlock(img, inverse(transform(readBlock(img, width, height), factor), factor), width, height);
    }

    static private double[][][] readBlock(Raster raster, int width, int height) {
        int numBands = raster.getNumBands();
        double[][][] block = new double[numBands][BLOCK_SIZE][BLOCK_SIZE];

        for (int x = 0; x < BLOCK_SIZE; x++)
            for (int y = 0; y < BLOCK_SIZE; y++)
                if (x + width < raster.getWidth() && y + height < raster.getHeight()) {
                    for (int color = 0; color < numBands; color++) {
                        block[color][x][y] = -NORMALIZER;
                        block[color][x][y] += raster.getSampleDouble(x + width, y + height, color);
                    }
                    if (numBands == 3)
                        convertToYCC(block, x, y);
                }
        return block;
    }

    static private void writeBlock(WritableRaster raster, double[][][] block, int width, int height) {
        int numBands = raster.getNumBands();

        for (int x = 0; x < BLOCK_SIZE; x++)
            for (int y = 0; y < BLOCK_SIZE; y++)
                if (x + width < raster.getWidth() && y + height < raster.getHeight()) {
                    if (numBands == 3)
                        convertToRGB(block, x, y);
                    for (int color = 0; color < numBands; color++) {
                        block[color][x][y] = ImagesUtil.rangePx(block[color][x][y] + NORMALIZER);
                        raster.setSample(x + width, y + height, color, block[color][x][y]);
                   }
                }
    }

    static private double[][][] transform(final double[][][] block, final double factor) {
        final DCT2D dct = new DCT2D();
        for (int color = 0; color < block.length; color++) {
            int[][] qTable = color == 0 ? QT_LUMINANCE : QT_CHROMINANCE;

            // TRANSFORM
            block[color] = dct.transform(block[color]);
            // QUANTIZE
            for (int x = 0; x < BLOCK_SIZE; x++)
                for (int y = 0; y < BLOCK_SIZE; y++)
                    block[color][x][y] = Math.round(block[color][x][y] / (qTable[x][y] * factor));
        }
        return block;
    }

    static private double[][][] inverse(final double[][][] block, final double factor) {
        final DCT2DInverse inv = new DCT2DInverse();
        for (int color = 0; color < block.length; color++) {
            int[][] qTable = color == 0 ? QT_LUMINANCE : QT_CHROMINANCE;

            // DE-QUANTIZE
            for (int x = 0; x < BLOCK_SIZE; x++)
                for (int y = 0; y < BLOCK_SIZE; y++)
                    block[color][x][y] *= qTable[x][y] * factor;
            // INVERSE
            block[color] = inv.transform(block[color]);
        }
        return block;
    }


    static private void convertToYCC(double[][][] block, int x, int y) {
        double lum = 0.299 * block[0][x][y] + 0.587 * block[1][x][y] + 0.114 * block[2][x][y];
        double Cb = -0.147 * block[0][x][y] - 0.289 * block[1][x][y] + 0.436 * block[2][x][y];
        double Cr = 0.615 * block[0][x][y] - 0.515 * block[1][x][y] - 0.100 * block[2][x][y];
        block[0][x][y] = lum;
        block[1][x][y] = Cb;
        block[2][x][y] = Cr;
    }

    static private void convertToRGB(double[][][] block, int x, int y) {
        double red = block[0][x][y] + 1.140 * block[2][x][y];
        double blu = block[0][x][y] - 0.395 * block[1][x][y] - 0.581 * block[2][x][y];
        double gre = block[0][x][y] + 2.032 * block[1][x][y];
        block[0][x][y] = red;
        block[1][x][y] = blu;
        block[2][x][y] = gre;
    }

}
