package berack96.multimedia;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Classe di utilita' contenente metodi per la manipolazione di immagini e per calcoli di trasformate
 */
public class ImagesUtil {

    /**
     * Massimo valore dei colori
     */
    public static final int MAX_COLOR = 255;

    /**
     * Coseno da usare per la trasformata Discrete Cosine Transform
     * @param ind l'indice dei valori dell'array che si cicla
     * @param ind2 l'indice del valore che si sta' calcolando
     * @param num il numero totale di valori discreti
     * @return il coseno calcolato
     */
    public static double cosDCT(double ind, double ind2, int num) {
        return Math.cos(((2*ind + 1) * Math.PI * ind2) / (2 * num));
    }

    /**
     * Permette di calcolare il valore di alpha per le varie trasformate
     * @param index l'indice dell'array che si sta calcola
     * @param length la lunghezza dell'array chje si sta calcolando
     * @return il valore di alpha
     */
    public static double getAlpha(int index, int length) {
        return Math.sqrt((index == 0 ? 1.0 : 2.0) / length);
    }

    /**
     * Fa in modo di forzare il valore fra il minimo e il massimo valore consentito
     * @param val il valore che si deve controllare
     * @param min il valore minimo che puo' avere
     * @param max il valore massimo che puo' avere
     * @return il valore
     */
    public static int range(int val, int min, int max) {
        return Math.min(max, Math.max(val, min));
    }

    /**
     * Fa in modo di forzare il valore fra il minimo e il massimo valore consentito per il colore del pixel
     * @param val il valore che si deve controllare
     * @return il valore
     */
    public static int rangePx(double val) {
        return Math.min(MAX_COLOR, Math.max((int)Math.round(val), 0));
    }

    /**
     * Funzione che permette di far partire un processo su una qualche immagine ed aspettare che finisca.<br>
     * Questa funzione permette di avere un log che indica lo stato della computazione.
     * @param name    il nome da dare nel log
     * @param program il programma da far partire
     * @return il risultato del programma
     */
    public static BufferedImage waitProcess(PrintStream out, String name, Supplier<BufferedImage> program) {
        long time = System.currentTimeMillis();
        final AtomicReference<BufferedImage> processed = new AtomicReference<>();

        System.out.println("Starting processing for " + name);
        Thread thread = new Thread(() -> processed.set(program.get()), THREADS_NAME);
        thread.start();

        do {
            try { Thread.sleep(10); } catch (Exception ignore) {}
            out.print(Math.round(((float) count.get() / tot) * 100) + "%\r");
        } while (thread.isAlive());

        time = System.currentTimeMillis() - time;
        System.out.println("Ended in " + ((float) time / 1000) + "sec");
        tot = 0;
        return processed.get();
    }

    /**
     * Variabile che serve a settare il numero di threads che vengono creati quando
     * viene richiamato il metodo forEachPixel.<br>
     * In caso il valore sia < 2,  non verranno creati thread.
     */
    public static int maxThreads = 6;

    /**
     * Indica il nome dei vari thread che vengono avviati durante il forEach.
     */
    public final static String THREADS_NAME = "ImageProcessing";

    /**
     * Una variabile che viene usata per conteggiare quanti pixel (o blocchi) sono stati elaborati.<br>
     * Qesta viene aggiornata ogni volta che viene fatta una iterazione con il metodo forEachPixel
     */
    public final static AtomicInteger count = new AtomicInteger();

    /**
     * Una variabile che viene usata per indicare quanti pixel totali devono essere elaborati.<br>
     * Qesta viene aggiornata ogni volta che viene richiamato il metodo forEachPixel
     */
    private static int tot = 0;

    /**
     * Metodo che serve per iterare ogni pixel del raster passato e applicargli la funzione specificata<br>
     * Il consumer passato avra' in input le coordinate del pixel sottoforma (x,y)<br>
     * In base a come viene settata la variabile {@link #maxThreads} il metodo potrebbe creare dei
     * threads per l'elaborazione dei pixel<br>
     * Questo metodo e' equvalente a {@link #forEachPixel(Raster, int, BiConsumer)} con delta = 1.
     * @param raster il raster da elaborare
     * @param consumer la funzione da applicargli
     */
    public static void forEachPixel(Raster raster, BiConsumer<Integer, Integer> consumer) {
        forEachPixel(raster, 1, consumer);
    }

    /**
     * Metodo che serve per iterare ogni pixel del raster passato e applicargli la funzione specificata<br>
     * Il consumer passato avra' in input le coordinate del pixel sottoforma (x,y)<br>
     * In base a come viene settata la variabile {@link #maxThreads} il metodo potrebbe creare dei
     * threads per l'elaborazione dei pixel<br>
     * Questo metodo accetta un delta che serve per spostarsi da un pixel a quello successivo saltando eventuali pixel intermedi<br>
     * Se si vogliono iterare tutti i pixel allra basta passare il delta a 1.
     * @param raster il raster da elaborare
     * @param delta di quanto mi devo spostare dopo ogni elaborazione per ogni asse
     * @param consumer la funzione da applicargli
     */
    public static synchronized void forEachPixel(final Raster raster, final int delta, final BiConsumer<Integer, Integer> consumer) {
        count.set(0);
        tot = raster.getWidth() * raster.getHeight() / (delta * delta);
        final int deltaF = Math.max(delta, 1);
        final int numThreads = Math.min(maxThreads, Runtime.getRuntime().availableProcessors());

        if (numThreads < 2)
            forEachPixel(raster, deltaF, 0, 1, consumer);
        else {
            Thread[] threads = new Thread[numThreads - 1];
            for (int i = 0; i < threads.length; i++) {
                final int num = i + 1;
                threads[i] = new Thread(() -> forEachPixel(raster, deltaF, num, numThreads, consumer), THREADS_NAME);
                threads[i].start();
            }

            forEachPixel(raster, deltaF, 0, numThreads, consumer);
            for (Thread t : threads)
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }
    }

    /**
     * Metodo privato che serve per i threads.<br>
     * Itera per tutta l'immagine o solamente per alcune righe
     * @param raster l'immagine da iterare
     * @param delta quanti pixel si devono saltare dopo ogni iterazione
     * @param num il numero del thread (ID)
     * @param threads il numero di threads che svolgono la stessa operazione
     * @param consumer l'operazione da svolgere
     */
    private static void forEachPixel(Raster raster, int delta, int num, int threads, BiConsumer<Integer, Integer> consumer) {
        for (int height = num * delta; height < raster.getHeight(); height += delta * threads)
            for (int width = 0; width < raster.getWidth(); width += delta) {
                consumer.accept(width, height);
                count.incrementAndGet();
            }
    }
}
