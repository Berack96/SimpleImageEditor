package berack96.multimedia;

/**
 * Interfaccia generica per fare delle trasformate matematiche
 * @param <O> L'oggetto della trasformata
 * @param <R> L'oggetto trasformato
 */
public interface Transform<O, R> {
    /**
     * Questo metodo applica la trasformata sull'oggetto passato in input
     * @param obj l'oggetto a cui applicare la trasformata
     * @return l'oggetto risultante
     */
    R transform(O obj);
}
