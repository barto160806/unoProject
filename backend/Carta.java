package backend;

/**
 * Rappresenta una carta di UNO, definita da un colore e da un valore.
 */
public class Carta {

    /** Il colore della carta. */
    protected Colore coloreCarta;

    /** Il valore numerico o tipo speciale della carta. */
    protected Valore valoreCarta;

    /**
     * Crea una nuova carta con colore e valore specificati.
     * 
     * @param coloreCarta il colore della carta (o NESSUNO per i jolly)
     * @param valoreCarta il valore della carta
     */
    public Carta(Colore coloreCarta, Valore valoreCarta) {
        this.coloreCarta = coloreCarta;
        this.valoreCarta = valoreCarta;
    }

    /**
     * Restituisce il colore della carta.
     * 
     * @return il colore attuale
     */
    public Colore getColoreCarta() {
        return coloreCarta;
    }

    /**
     * Cambia il colore della carta (usato quando si gioca un jolly).
     * 
     * @param coloreCarta il nuovo colore da impostare
     */
    public void setColoreCarta(Colore coloreCarta) {
        this.coloreCarta = coloreCarta;
    }

    /**
     * Restituisce il valore della carta.
     * 
     * @return il valore della carta
     */
    public Valore getValoreCarta() {
        return valoreCarta;
    }

    /**
     * Restituisce la descrizione testuale della carta (es. "ZERO ROSSO").
     * 
     * @return stringa con valore e colore della carta
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(valoreCarta).append(" " + coloreCarta);
        return sb.toString();
    }
}
