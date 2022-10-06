
package berack96.multimedia.resize;

import java.awt.image.BufferedImage;

import berack96.multimedia.Transform;

/**
 * Interfaccia usata per il resizing delle immagini
 */
public abstract class Resizing implements Transform<BufferedImage, BufferedImage> {
    protected double ratioWidth = 1;
    protected double ratioHeight = 1;

    public Resizing(double ratioWidth, double ratioHeight) {
        setRatio(ratioWidth, ratioHeight);
    }

    /**
     * Setta il ratio per il resampling dell'immagine. Esso e' un numero > 0.<br>
     * Per valori 0 > x > 1 avverra' un down-scaling<br>
     * Per valori x > 1 avverra' un up-scaling
     * @param ratioWidth il ratio della larghezza di resampling
     * @param ratioHeight il ratio dell'altezza di resampling
     * @return Questa classe in modo da poter concatenare le chiamate
     */
    public Resizing setRatio(double ratioWidth, double ratioHeight) {
        this.ratioWidth = ratioWidth > 0 ? ratioWidth : 1;
        this.ratioHeight = ratioHeight > 0 ? ratioHeight : 1;
        return this;
    }

    /**
     * Crea una nuova immagine ingrandita/rimpicciolita in base al valore della variabile ratio
     * @param original l'immagine originale
     * @return la nuova immagine vuota
     */
    protected BufferedImage createResized(BufferedImage original) {
        int newWidth = (int) (original.getWidth() * ratioWidth);
        int newHeight = (int) (original.getHeight() * ratioHeight);
        return new BufferedImage(newWidth, newHeight, original.getType());
    }
}
