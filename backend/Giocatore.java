package backend;

import java.util.List;
import java.util.ArrayList;

/**
 * Classe astratta che rappresenta un giocatore di UNO (umano o bot).
 * Gestisce la mano di carte, il punteggio e definisce le azioni di gioco.
 */
public abstract class Giocatore implements Comparable<Giocatore> {

    /** Le carte in mano al giocatore. */
    protected List<Carta> mano = new ArrayList<>();

    /** Nome del giocatore. */
    public String nome;

    /** Punteggio accumulato nella partita a punti. */
    protected int punteggio;

    /** Indica se il giocatore ha dichiarato UNO in tempo. */
    public boolean haDichiaratoUnoSuccesso = false;

    /**
     * Crea un giocatore con il nome specificato.
     * 
     * @param nome il nome del giocatore
     */
    public Giocatore(String nome) {
        this.nome = nome;
    }

    /**
     * Sceglie quale carta giocare nel proprio turno.
     * 
     * @param cartaInCima       la carta in cima agli scarti
     * @param coloreCorrente    il colore attualmente attivo
     * @param prossimoGiocatore il nome del giocatore successivo
     * @return la carta scelta da giocare, oppure null per pescare
     */
    public abstract Carta scegliMossa(Carta cartaInCima, Colore coloreCorrente, String prossimoGiocatore);

    /**
     * Sceglie un avversario con cui scambiare le carte per la regola del 7.
     * 
     * @param giocatore            il giocatore che ha giocato il 7
     * @param giocatoriDisponibili tutti i giocatori della partita
     * @return il giocatore bersaglio con cui scambiare la mano
     */
    public abstract Giocatore scegliGiocatoreDaScambiare(Giocatore giocatore, List<Giocatore> giocatoriDisponibili);

    /**
     * Sceglie il nuovo colore di gioco dopo aver scartato un jolly.
     * 
     * @return il colore scelto
     */
    public abstract Colore scegliColore();

    /**
     * Decide se lanciare la sfida sul +4 giocato da un avversario.
     * 
     * @param carteAvversario le carte rimaste all'avversario
     * @return true se lancia la sfida, false altrimenti
     */
    public abstract boolean effettuaChallenge(int carteAvversario);

    /**
     * Indica se il giocatore dichiara UNO quando rimane con una sola carta.
     * 
     * @return true se dichiara UNO, false se se ne dimentica
     */
    public abstract boolean haDichiaratoUno();

    /**
     * Decide se contestare un avversario che ha una sola carta ma non ha detto UNO.
     * 
     * @param bersaglio l'avversario da contestare
     * @return true se contesta, false altrimenti
     */
    public abstract boolean contestazioneUno(Giocatore bersaglio);

    /**
     * Controlla se una carta si puo' giocare sopra quella in cima agli scarti.
     * 
     * @param cartaDaGiocare la carta da verificare
     * @param cartaInCima    la carta in cima agli scarti
     * @param coloreAttuale  il colore attivo sul tavolo
     * @return true se la carta e' giocabile, false altrimenti
     */
    public boolean verificaMossa(Carta cartaDaGiocare, Carta cartaInCima, Colore coloreAttuale) {
        // Stesso colore o stesso valore
        if (cartaDaGiocare.coloreCarta == coloreAttuale || cartaDaGiocare.valoreCarta == cartaInCima.valoreCarta) {
            return true;
        }
        // I jolly si possono sempre giocare
        if (cartaDaGiocare.valoreCarta == Valore.WILD || cartaDaGiocare.valoreCarta == Valore.WILD_DRAW_FOUR) {
            return true;
        }
        return false;
    }

    /**
     * Controlla se il giocatore ha almeno una carta giocabile in mano.
     * 
     * @param cartaInCima   la carta in cima agli scarti
     * @param coloreAttuale il colore attualmente attivo
     * @return true se ha mosse valide, false se deve pescare
     */
    public boolean haMosseGiocabili(Carta cartaInCima, Colore coloreAttuale) {
        for (Carta carta : mano) {
            if (verificaMossa(carta, cartaInCima, coloreAttuale)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica se il giocatore possiede almeno una carta del colore specificato
     * (usato per il challenge del +4).
     * 
     * @param coloreAttuale il colore da cercare
     * @return true se ha almeno una carta di quel colore, false altrimenti
     */
    public boolean haCartaDiColore(Colore coloreAttuale) {
        for (Carta carta : mano) {
            if (carta.coloreCarta == coloreAttuale) {
                return true;
            }
        }
        return false;
    }

    /**
     * Aggiunge una carta pescata alla mano.
     * 
     * @param carta la carta da aggiungere
     */
    public void aggiungiCarta(Carta carta) {
        mano.add(carta);
    }

    /**
     * Rimuove una carta dalla mano e la gioca.
     * 
     * @param cartaScelta la carta da giocare
     * @return la carta giocata
     */
    public Carta giocaCarta(Carta cartaScelta) {
        mano.remove(cartaScelta);
        return cartaScelta;
    }

    /**
     * Calcola il totale dei punti delle carte rimaste in mano.
     * 
     * @return il totale dei punti
     */
    public int calcolaPuntiMano() {
        int totalePunti = 0;
        for (Carta carta : mano) {
            totalePunti += carta.valoreCarta.getPunti();
        }
        return totalePunti;
    }

    /**
     * Confronta due giocatori in ordine alfabetico per nome.
     * 
     * @param giocatore l'altro giocatore da confrontare
     * @return esito del confronto alfabetico
     */
    @Override
    public int compareTo(Giocatore giocatore) {
        return this.nome.compareTo(giocatore.nome);
    }

    /**
     * Restituisce il numero di carte in mano.
     * 
     * @return numero di carte
     */
    public int getNumeroCarte() {
        return mano.size();
    }

    /**
     * Restituisce la lista delle carte in mano.
     * 
     * @return la lista di carte
     */
    public List<Carta> getMano() {
        return mano;
    }

    /**
     * Restituisce le carte in mano formattate come stringa numerata.
     * 
     * @return stringa con l'elenco delle carte
     */
    public String getManoString() {
        short i = 0;
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (Carta carta : mano) {
            sb.append(++i).append(". ").append(carta);
            if (i != getNumeroCarte()) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
