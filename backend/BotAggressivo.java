package backend;

import java.util.Random;
import java.util.Arrays;
import java.util.List;

/**
 * Bot che adotta una strategia aggressiva, privilegiando le carte speciali
 * e i jolly per mettere in difficolta' gli avversari. Sceglie sempre il
 * colore piu' presente nella propria mano e, nello scambio Seven-Zero,
 * punta all'avversario con meno carte. Le decisioni casuali (UNO, contestazioni
 * e challenge)
 * restituiscono un esito positivo nel 70% dei casi.
 */
public class BotAggressivo extends Giocatore {

    /** Contatore progressivo per assegnare nomi univoci ai bot aggressivi. */
    public static int contatoreGiocatore;

    private Valore[] listaValori = Valore.values();
    private List<Valore> listaJolly = List.of(Arrays.copyOfRange(listaValori, 10, 15));

    /**
     * Crea un nuovo bot aggressivo con nome univoco progressivo.
     */
    public BotAggressivo() {
        super("Bot Aggressivo " + ++contatoreGiocatore);
    }

    /**
     * Sceglie la carta da giocare dando priorita' a jolly e carte speciali;
     * se non ne ha, gioca la prima carta valida disponibile.
     * 
     * @param cartaInCima       la carta in cima agli scarti
     * @param coloreCorrente    il colore attualmente attivo
     * @param prossimoGiocatore il nome del prossimo giocatore
     * @return la carta scelta, oppure null se deve pescare
     */
    @Override
    public Carta scegliMossa(Carta cartaInCima, Colore coloreCorrente, String prossimoGiocatore) {
        Carta cartaScelta = getMano()
                .stream()
                .filter(carta -> verificaMossa(carta, cartaInCima, coloreCorrente))
                .filter(carta -> listaJolly.contains(carta.valoreCarta))
                .findFirst()
                .orElseGet(() -> getMano()
                        .stream()
                        .filter(carta -> verificaMossa(carta, cartaInCima, coloreCorrente))
                        .findFirst()
                        .orElse(null));

        return cartaScelta;
    }

    /**
     * Sceglie l'avversario con meno carte in mano per lo scambio della regola del
     * 7.
     * 
     * @param corrente  il bot che ha giocato il 7
     * @param giocatori tutti i giocatori della partita
     * @return l'avversario con il minor numero di carte
     */
    @Override
    public Giocatore scegliGiocatoreDaScambiare(Giocatore corrente, List<Giocatore> giocatori) {
        Giocatore giocatoreConMenoCarte = corrente;
        giocatoreConMenoCarte = giocatori.stream()
                .filter(giocatore -> !giocatore.equals(corrente))
                .reduce((giocatore1, giocatore2) -> giocatore1.getNumeroCarte() < giocatore2.getNumeroCarte()
                        ? giocatore1
                        : giocatore2)
                .orElse(giocatoreConMenoCarte);
        return giocatoreConMenoCarte;
    }

    /**
     * Sceglie il colore piu' presente nella propria mano dopo aver giocato un
     * jolly.
     * 
     * @return il colore con piu' carte in mano
     */
    @Override
    public Colore scegliColore() {
        int rosso = 0;
        int blu = 0;
        int giallo = 0;
        int verde = 0;
        Colore coloreScelto = null;

        for (Carta carta : getMano()) {
            switch (carta.coloreCarta) {
                case ROSSO:
                    rosso += 1;
                    break;
                case BLU:
                    blu += 1;
                    break;
                case VERDE:
                    verde += 1;
                    break;
                case GIALLO:
                    giallo += 1;
                    break;
                default:
                    break;
            }
        }

        int coloreMassimo = Math.max(rosso, blu);
        int coloreMassimo2 = Math.max(verde, giallo);
        int coloreFinale = Math.max(coloreMassimo, coloreMassimo2);

        if (coloreFinale == rosso) {
            coloreScelto = Colore.ROSSO;
        } else if (coloreFinale == blu) {
            coloreScelto = Colore.BLU;
        } else if (coloreFinale == verde) {
            coloreScelto = Colore.VERDE;
        } else if (coloreFinale == giallo) {
            coloreScelto = Colore.GIALLO;
        } else {
            coloreScelto = Colore.ROSSO;
        }
        return coloreScelto;
    }

    /**
     * Decide se sfidare il +4: accetta sempre se l'avversario ha poche carte,
     * altrimenti lancia la sfida con il 70% di probabilita'.
     * 
     * @param carteAvversario le carte rimaste all'avversario
     * @return true se lancia la sfida, false altrimenti
     */
    @Override
    public boolean effettuaChallenge(int carteAvversario) {
        boolean effettoProbabilita = probabilita();
        if (getMano().size() < 3) {
            return false;
        } else if (carteAvversario < 3) {
            return true;
        } else {
            return effettoProbabilita;
        }
    }

    /**
     * Dichiara UNO con il 70% di probabilita' di ricordarsi.
     * 
     * @return true se dichiara UNO, false se se ne dimentica
     */
    @Override
    public boolean haDichiaratoUno() {
        return probabilita();
    }

    /**
     * Contesta un avversario che non ha detto UNO, con il 70% di probabilita'.
     * 
     * @param bersaglio l'avversario da contestare
     * @return true se contesta, false altrimenti
     */
    @Override
    public boolean contestazioneUno(Giocatore bersaglio) {
        return probabilita();
    }

    /**
     * Genera un esito positivo con il 70% di probabilita'.
     * 
     * @return true nel 70% dei casi, false altrimenti
     */
    public boolean probabilita() {
        Random random = new Random();
        int numeroCasuale = random.nextInt(100);
        return numeroCasuale < 70;
    }
}
