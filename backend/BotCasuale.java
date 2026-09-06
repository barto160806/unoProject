package backend;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Bot con una strategia semplice: gioca la prima carta valida che trova,
 * mentre sceglie casualmente colori, avversari e decisioni come
 * dichiarazione UNO, contestazione e challenge.
 */
public class BotCasuale extends Giocatore {

    /** Contatore progressivo per assegnare nomi univoci ai bot casuali. */
    public static int contatoreGiocatore;

    /**
     * Crea un nuovo bot casuale con nome univoco progressivo.
     */
    public BotCasuale() {
        super("Bot casuale " + ++contatoreGiocatore);
    }

    /**
     * Gioca la prima carta valida che trova nella mano.
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
                .findFirst()
                .orElse(null);

        return cartaScelta;
    }

    /**
     * Sceglie a caso un avversario con cui scambiare le carte per la regola del 7.
     * 
     * @param giocatore            il bot che ha giocato il 7
     * @param giocatoriDisponibili tutti i giocatori della partita
     * @return un avversario estratto casualmente
     */
    @Override
    public Giocatore scegliGiocatoreDaScambiare(Giocatore giocatore, List<Giocatore> giocatoriDisponibili) {
        Random random = new Random();
        List<Giocatore> avversari = giocatoriDisponibili.stream().filter(avversario -> !avversario.equals(giocatore))
                .toList();
        return avversari.get(random.nextInt(avversari.size()));
    }

    /**
     * Sceglie un colore a caso tra quelli presenti nella propria mano
     * dopo aver giocato un jolly. Se non ha carte colorate, ne sceglie uno
     * qualsiasi.
     * 
     * @return il colore scelto
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

    /**
     * Lancia una moneta: restituisce true o false con il 50% di probabilita'.
     * 
     * @return esito casuale
     */
    public boolean randomBooleanCode() {
        Random random = new Random();
        return random.nextBoolean();
    }

    /**
     * Decide a caso se sfidare il +4 giocato da un avversario.
     * 
     * @param carteAvversario le carte rimaste all'avversario
     * @return true se lancia la sfida, false altrimenti
     */
    @Override
    public boolean effettuaChallenge(int carteAvversario) {
        return randomBooleanCode();
    }

    /**
     * Dichiara UNO con il 50% di probabilita' di ricordarsi.
     * 
     * @return true se dichiara UNO, false se se ne dimentica
     */
    @Override
    public boolean haDichiaratoUno() {
        return randomBooleanCode();
    }

    /**
     * Contesta a caso un avversario che non ha detto UNO.
     * 
     * @param bersaglio l'avversario da contestare
     * @return true se contesta, false altrimenti
     */
    @Override
    public boolean contestazioneUno(Giocatore bersaglio) {
        return randomBooleanCode();
    }
}
