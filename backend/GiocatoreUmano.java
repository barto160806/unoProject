package backend;

import java.util.List;

/**
 * Rappresenta un giocatore umano. Le decisioni di gioco vengono gestite
 * direttamente tramite l'interfaccia grafica.
 */
public class GiocatoreUmano extends Giocatore {

    private static int contatoreGiocatori;

    /**
     * Crea un giocatore umano con un nome personalizzato.
     * 
     * @param nome il nome del giocatore
     */
    public GiocatoreUmano(String nome) {
        super(nome);
    }

    /**
     * Costruttore di default che assegna un nome automatico (es. "Giocatore 1").
     */
    public GiocatoreUmano() {
        super("Giocatore " + ++contatoreGiocatori);
    }

    /**
     * La mossa viene scelta cliccando sulla carta nella GUI, quindi restituisce
     * null.
     * 
     * @param cartaInCima       la carta in cima agli scarti
     * @param coloreCorrente    il colore attivo
     * @param prossimoGiocatore nome del giocatore successivo
     * @return null (l'azione e' comandata dalla grafica)
     */
    @Override
    public Carta scegliMossa(Carta cartaInCima, Colore coloreCorrente, String prossimoGiocatore) {
        return null;
    }

    /**
     * Restituisce un colore di default. La vera scelta avviene tramite il pop-up
     * grafico.
     * 
     * @return colore di default
     */
    @Override
    public Colore scegliColore() {
        return Colore.ROSSO;
    }

    /**
     * La scelta del giocatore con cui scambiare le carte avviene tramite finestra
     * grafica.
     * 
     * @param giocatore            il giocatore corrente
     * @param giocatoriDisponibili tutti i giocatori
     * @return null (gestito dalla GUI)
     */
    @Override
    public Giocatore scegliGiocatoreDaScambiare(Giocatore giocatore, List<Giocatore> giocatoriDisponibili) {
        return null;
    }

    /**
     * La decisione di sfidare il +4 viene gestita tramite la finestra di conferma.
     * 
     * @param carteAvversario numero di carte dell'avversario
     * @return false di default
     */
    @Override
    public boolean effettuaChallenge(int carteAvversario) {
        return false;
    }

    /**
     * Restituisce l'esito della dichiarazione di UNO registrata dal pulsante
     * grafico.
     * 
     * @return true se il giocatore ha premuto in tempo il tasto UNO
     */
    @Override
    public boolean haDichiaratoUno() {
        return haDichiaratoUnoSuccesso;
    }

    /**
     * La contestazione di UNO viene gestita dal pulsante grafico dedicato.
     * 
     * @param bersaglio l'avversario da contestare
     * @return false di default
     */
    @Override
    public boolean contestazioneUno(Giocatore bersaglio) {
        return false;
    }
}
