package backend;

/**
 * Rappresenta i colori delle carte di UNO (Rosso, Giallo, Verde, Blu)
 * e il valore NESSUNO per i jolly.
 */
public enum Colore {
    ROSSO("rosso"),
    GIALLO("giallo"),
    VERDE("verde"),
    BLU("blu"),
    NESSUNO("nessuno");

    private final String nome;

    /**
     * Crea un colore associandogli il nome.
     * 
     * @param nome il nome del colore
     */
    private Colore(String nome) {
        this.nome = nome;
    }

    /**
     * Restituisce il nome del colore.
     * 
     * @return il nome del colore
     */
    public String getNome() {
        return nome;
    }
}
