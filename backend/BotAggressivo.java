package backend;

import java.util.Random;
import java.util.Arrays;
import java.util.List;

/**
 * Rappresenta un Bot con strategia "Aggressiva".
 * Questo bot:
 * - Dà priorità strategica alle carte Jolly e speciali (+2, +4, Salta,
 * Inverti) per ostacolare gli avversari.
 * - Nello scambio Seven-Zero sceglie sempre l'avversario con meno carte in
 * mano.
 * - Nella scelta del colore sceglie strategicamente il colore maggiormente
 * presente nella propria mano.
 * - Adotta decisioni probabilistiche avanzate (70% di successo per
 * dichiarazione
 * di UNO,
 * contestazioni e challenge).
 */
public class BotAggressivo extends Giocatore {

    /** Contatore per l'assegnazione automatica del nome al bot. */
    public static int contatoreGiocatore;

    private Valore[] listaValori = Valore.values();
    private List<Valore> listaJolly = List.of(Arrays.copyOfRange(listaValori, 10, 15));

    /**
     * Costruttore di default che assegna un nome univoco.
     */
    public BotAggressivo() {
        super("Bot Aggressivo " + ++contatoreGiocatore);
    }

    /**
     * Sceglie la mossa privilegiando le carte speciali/jolly per mettere in
     * difficoltà gli avversari.
     * 
     * @param cartaInCima       la carta in cima agli scarti
     * @param coloreCorrente    il colore attualmente attivo
     * @param prossimoGiocatore nome del giocatore successivo nel turno
     * @return la prima carta valida oppure null per pescare
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
     * Sceglie l'avversario che possiede il minor numero di carte per massimizzare
     * il vantaggio nello scambio (regola del 7).
     * 
     * @param corrente  il bot stesso
     * @param giocatori la lista di tutti i partecipanti
     * @return il giocatore con meno carte
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
     * Sceglie il colore piu' frequente tra le carte presenti nella propria mano.
     * 
     * @return il colore piu' comune nella mano
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
     * Valuta strategicamente se lanciare la challenge al +4:
     * accetta sempre se l'avversario ha meno di 3 carte o con probabilità del 70%.
     * 
     * @param carteAvversario numero di carte dell'avversario che ha giocato il +4
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
     * Dichiara UNO con una probabilità del 70%.
     * 
     * @return true se dichiara UNO, false altrimenti
     */
    @Override
    public boolean haDichiaratoUno() {
        return probabilita();
    }

    /**
     * Contesta la mancata dichiarazione di UNO con una probabilità del 70%.
     * 
     * @param bersaglio l'avversario con 1 carta
     * @return true se contesta, false altrimenti
     */
    @Override
    public boolean contestazioneUno(Giocatore bersaglio) {
        return probabilita();
    }

    // Genera true con il 70% di probabilita'
    public boolean probabilita() {
        Random random = new Random();
        int numeroCasuale = random.nextInt(100);
        return numeroCasuale < 70;
    }
}
