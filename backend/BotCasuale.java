package backend;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Rappresenta un Bot con strategia "Casuale".
 * Questa bot:
 * - Gioca la prima carta valida trovata nella propria mano.
 * - Sceglie un colore a caso tra quelli che possiede in mano.
 * - Nello scambio Seven-Zero sceglie un avversario a caso.
 * - Prende decisioni con la probabilità casuale del 50% (testa o croce).
 */
public class BotCasuale extends Giocatore {

    /** Contatore per l'assegnazione automatica del nome al bot. */
    public static int contatoreGiocatore;

    /**
     * Costruttore di default che assegna un nome univoco.
     */
    public BotCasuale() {
        super("Bot casuale " + ++contatoreGiocatore);
    }

    /**
     * Seleziona la prima carta giocabile trovata nella propria mano.
     * 
     * @param cartaInCima       la carta in cima agli scarti
     * @param coloreCorrente    il colore attualmente attivo
     * @param prossimoGiocatore nome del giocatore successivo
     * @return la prima carta valida oppure null per pescare
     */
    @Override
    public Carta scegliMossa(Carta cartaInCima, Colore coloreCorrente, String prossimoGiocatore) {
        Carta cartaScelta = getMano()
                .stream()
                .filter(carta -> verificaMossa(carta, cartaInCima, coloreCorrente))
                .findFirst()
                .orElse(null);

        return cartaScelta;
    }

    /**
     * Sceglie un avversario a caso tra quelli disponibili per lo scambio carte
     * (regola del 7).
     * 
     * @param giocatore            il bot stesso
     * @param giocatoriDisponibili la lista di tutti i giocatori
     * @return un Giocatore scelto casualmente
     */
    @Override
    public Giocatore scegliGiocatoreDaScambiare(Giocatore giocatore, List<Giocatore> giocatoriDisponibili) {
        Random random = new Random();
        List<Giocatore> avversari = giocatoriDisponibili.stream().filter(avversario -> !avversario.equals(giocatore))
                .toList();
        return avversari.get(random.nextInt(avversari.size()));
    }

    /**
     * Sceglie casualmente un colore tra quelli che possiede in mano.
     * 
     * @return il colore estratto.
     */
    @Override
    public Colore scegliColore() {
        Random random = new Random();
        Colore[] arrayColori = Colore.values();
        List<Colore> coloriValidi = List.of(Arrays.copyOfRange(arrayColori, 0, 4));
        boolean haCarteColorate = getMano()
                .stream()
                .anyMatch(carta -> coloriValidi.contains(carta.coloreCarta));
        Colore coloreScelto = null;

        if (haCarteColorate) {
            boolean statoWhile = false;
            do {
                int numeroCasuale = random.nextInt(1, 5);
                switch (numeroCasuale) {
                    case 1:
                        coloreScelto = Colore.ROSSO;
                        break;
                    case 2:
                        coloreScelto = Colore.BLU;
                        break;
                    case 3:
                        coloreScelto = Colore.VERDE;
                        break;
                    case 4:
                        coloreScelto = Colore.GIALLO;
                        break;
                }
                Colore coloreTemp = coloreScelto;
                statoWhile = getMano().stream()
                        .anyMatch(carta -> carta.coloreCarta == coloreTemp);
            } while (!statoWhile);
        } else {
            int indiceCasuale = random.nextInt(4);
            coloreScelto = arrayColori[indiceCasuale];
        }
        return coloreScelto;
    }

    // Genera un valore booleano casuale
    public boolean randomBooleanCode() {
        Random random = new Random();
        return random.nextBoolean();
    }

    /**
     * Decide casualmente se effettuare la challenge sul +4.
     * 
     * @param carteAvversario numero carte avversario
     * @return esito della decisione casuale
     */
    @Override
    public boolean effettuaChallenge(int carteAvversario) {
        return randomBooleanCode();
    }

    /**
     * Dichiara UNO con il 50% di probabilità.
     * 
     * @return esito della decisione casuale
     */
    @Override
    public boolean haDichiaratoUno() {
        return randomBooleanCode();
    }

    /**
     * Contesta la mancata dichiarazione di UNO con il 50% di probabilità.
     * 
     * @param bersaglio avversario con 1 carta.
     * @return esito della decisione casuale
     */
    @Override
    public boolean contestazioneUno(Giocatore bersaglio) {
        return randomBooleanCode();
    }
}
