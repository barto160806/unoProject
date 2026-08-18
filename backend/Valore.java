package backend;

/**
 * Rappresenta i valori e le azioni speciali delle carte di UNO con i rispettivi
 * punti.
 */
public enum Valore {
    ZERO(0),
    UNO(1),
    DUE(2),
    TRE(3),
    QUATTRO(4),
    CINQUE(5),
    SEI(6),
    SETTE(7),
    OTTO(8),
    NOVE(9),
    SKIP(20),
    REVERSE(20),
    DRAW_TWO(20),
    WILD(50),
    WILD_DRAW_FOUR(50);

    private final int punti;

    /**
     * Crea un valore associandogli i punti per il conteggio a fine round.
     * 
     * @param punti i punti della carta
     */
    Valore(int punti) {
        this.punti = punti;
    }

    /**
     * Restituisce i punti associati alla carta.
     * 
     * @return i punti della carta
     */
    public int getPunti() {
        return punti;
    }
}
