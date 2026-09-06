package gui;

import backend.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Finestra principale è il controller del gioco UNO.
 * Gestisce l'interfaccia grafica, il turno dei giocatori, i bot e le regole di
 * gioco.
 */
public class FinestraPrincipale extends JFrame {

    private static final Color COLORE_TAVOLO = new Color(27, 94, 32);

    // Pannelli principali dell'interfaccia
    private final PannelloAvversari pannelloAvversari;
    private final PannelloAzioni pannelloAzioni;
    private final PannelloManoGiocatore pannelloManoGiocatore;
    private final PannelloTavolo pannelloTavolo;

    // Contenitori e strutture dati del gioco
    private GiocatoreUmano giocatoreUmanoPrincipale;
    private List<Giocatore> giocatori = new ArrayList<>();
    private List<Carta> mazzoPesca = new ArrayList<>();
    private List<Carta> scarti = new ArrayList<>();
    private Map<Giocatore, Integer> mappaPunteggio = new HashMap<>();

    // Impostazioni e varianti della partita
    private boolean modalitaPunti = false;
    private int sogliaVittoria = 500;
    private boolean varianteStacking = false;
    private boolean varianteNumberRush = false;
    private boolean varianteSevenZero = false;

    // Stati del turno
    private int indiceGiocatoreCorrente = 0;
    private int direzione = 1;
    private int accumuloPesca = 0;
    private boolean partitaFinita = false;
    private boolean haPescatoNelTurno = false;
    private Colore colorePrecedente = Colore.NESSUNO;
    private Timer timerTurnoBot;

    /**
     * Costruttore della finestra principale: imposta le dimensioni (1280x720)
     * e assembla i pannelli dell'interfaccia grafica.
     */
    public FinestraPrincipale() {
        super("UNO");

        this.setSize(1280, 720);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        // Inizializzazione dei sotto-pannelli della GUI
        pannelloAvversari = new PannelloAvversari();
        pannelloTavolo = new PannelloTavolo();
        pannelloManoGiocatore = new PannelloManoGiocatore();
        pannelloAzioni = new PannelloAzioni();

        // Contenitore principale con layout a border
        JPanel pannelloPrincipale = new JPanel(new BorderLayout(15, 15));
        pannelloPrincipale.setBackground(COLORE_TAVOLO);
        pannelloPrincipale.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Disposizione dei pannelli nelle aree geografiche
        pannelloPrincipale.add(pannelloAvversari, BorderLayout.NORTH);
        pannelloPrincipale.add(pannelloTavolo, BorderLayout.CENTER);
        pannelloPrincipale.add(pannelloManoGiocatore, BorderLayout.SOUTH);
        pannelloPrincipale.add(pannelloAzioni, BorderLayout.EAST);

        this.setContentPane(pannelloPrincipale);

        // Collegamento degli ascoltatori degli eventi
        collegaEventiGUI();

        // Apertura popup di configurazione all'avvio
        SwingUtilities.invokeLater(() -> {
            apriConfigurazionePartita();
        });
    }

    // Mostra il popup per configurare modalita', varianti e giocatori
    private void apriConfigurazionePartita() {
        PopUpConfigurazione configurazionePartita = new PopUpConfigurazione();
        configurazionePartita.setVisible(true);

        if (configurazionePartita.isConfermata()) {
            modalitaPunti = configurazionePartita.isModalitaPunti();
            sogliaVittoria = configurazionePartita.getSogliaVittoria();
            varianteStacking = configurazionePartita.isVarianteStacking();
            varianteNumberRush = configurazionePartita.isVarianteNumberRush();
            varianteSevenZero = configurazionePartita.isVarianteSevenZero();

            inizializzaPartita(configurazionePartita.getGiocatoriConfig());

        } else if (giocatori.isEmpty()) {
            // Configurazione predefinita di backup se annullata all'avvio
            List<PopUpConfigurazione.GiocatoreConfig> defaultList = List.of(
                    new PopUpConfigurazione.GiocatoreConfig("Tu (Umano)", "Umano"),
                    new PopUpConfigurazione.GiocatoreConfig("Bot Casuale 1", "Bot Casuale"),
                    new PopUpConfigurazione.GiocatoreConfig("Bot Aggressivo", "Bot Aggressivo"),
                    new PopUpConfigurazione.GiocatoreConfig("Bot Casuale 2", "Bot Casuale"));
            inizializzaPartita(defaultList);
        }
    }

    // Inizializza i giocatori, i punteggi e avvia il primo round
    private void inizializzaPartita(List<PopUpConfigurazione.GiocatoreConfig> configurazioni) {
        giocatori.clear();
        mappaPunteggio.clear();
        giocatoreUmanoPrincipale = null;

        for (PopUpConfigurazione.GiocatoreConfig configurazione : configurazioni) {
            Giocatore g;
            if ("Bot Casuale".equals(configurazione.tipo)) {
                g = new BotCasuale();
            } else if ("Bot Aggressivo".equals(configurazione.tipo)) {
                g = new BotAggressivo();
            } else {
                GiocatoreUmano umano = new GiocatoreUmano(configurazione.nome);
                g = umano;
                if (giocatoreUmanoPrincipale == null) {
                    giocatoreUmanoPrincipale = umano;
                }
            }
            g.nome = configurazione.nome;
            giocatori.add(g);
            mappaPunteggio.put(g, 0);
        }

        if (giocatoreUmanoPrincipale == null && !giocatori.isEmpty()) {
            if (giocatori.get(0) instanceof GiocatoreUmano hum) {
                giocatoreUmanoPrincipale = hum;
            }
        }

        nuovoRound();
    }

    // Prepara un nuovo round (distribuisce 7 carte e posiziona la prima carta sugli
    // scarti)
    private void nuovoRound() {
        for (Giocatore g : giocatori) {
            g.getMano().clear();
            g.haDichiaratoUnoSuccesso = false;
        }
        pannelloTavolo.pulisciLogs();
        mazzoPesca = Mazzo.inizializzaMazzo();
        scarti = new ArrayList<>();
        direzione = 1;
        accumuloPesca = 0;
        indiceGiocatoreCorrente = 0;
        partitaFinita = false;
        haPescatoNelTurno = false;
        // Distribuzione iniziale di 7 carte a testa
        for (int i = 0; i < 7; i++) {
            for (Giocatore g : giocatori) {
                if (!mazzoPesca.isEmpty()) {
                    g.aggiungiCarta(pescaCartaMazzo());
                }
            }
        }

        // Prima carta sulla Pila Scarti
        Carta primaCarta = pescaCartaMazzo();
        while (primaCarta.getValoreCarta().ordinal() > Valore.NOVE.ordinal()) {
            mazzoPesca.add(primaCarta);
            Mazzo.mescola(mazzoPesca);
            primaCarta = pescaCartaMazzo();
        }
        scarti.add(primaCarta);

        pannelloAvversari.impostaGiocatori(giocatori);
        aggiornaStatoGrafico();
    }

    // Registra i listener per i pulsanti, il mazzo e le carte in mano
    private void collegaEventiGUI() {
        pannelloAzioni.registraActionListener(new AzioniPannelloListener());
        pannelloTavolo.registraMazzoListener(new AzioniPannelloListener());
        pannelloManoGiocatore.registraActionListener(new CartaManoListener());
    }

    // Gestisce l'azione di pesca dal mazzo o lo stacking
    private void eseguiAzionePesca() {
        Giocatore corrente = giocatori.get(indiceGiocatoreCorrente);
        if (!(corrente instanceof GiocatoreUmano)) {
            JOptionPane.showMessageDialog(this, "Attendi, è il turno di " + corrente.nome + "!", "Turno in Corso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (varianteStacking && accumuloPesca > 0) {
            int pescati = accumuloPesca;
            for (int i = 0; i < pescati; i++) {
                corrente.aggiungiCarta(pescaCartaMazzo());
            }
            accumuloPesca = 0;
            corrente.haDichiaratoUnoSuccesso = false;
            pannelloTavolo.aggiungiLog(
                    corrente.nome + " non risponde allo stacking e pesca " + pescati + " carte e salta il turno.");
            passaAlProssimoGiocatore();
        } else {
            if (haPescatoNelTurno) {
                JOptionPane.showMessageDialog(this,
                        "Hai già pescato! Puoi giocare una carta valida oppure saltare il turno.",
                        "Azione Non Valida", JOptionPane.WARNING_MESSAGE);
                return;
            }

            haPescatoNelTurno = true;
            Carta pescata = pescaCartaMazzo();
            corrente.aggiungiCarta(pescata);
            corrente.haDichiaratoUnoSuccesso = false;
            pannelloTavolo.aggiungiLog(corrente.nome + " ha pescato una carta.");

            if (corrente.verificaMossa(pescata, getCartaInCima(), getColoreAttuale())) {
                if (corrente.getNumeroCarte() > 2) {
                    int risposta = JOptionPane.showConfirmDialog(this,
                            "Hai pescato: " + pescata + ".\nÈ giocabile! Vuoi giocarla subito?",
                            "Carta Pescata Giocabile", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (risposta == JOptionPane.YES_OPTION) {
                        eseguiMossaGiocatore(corrente, pescata);
                        return;
                    }
                }
            }
        }

        aggiornaStatoGrafico();
    }

    // Gestisce la dichiarazione di UNO da parte del giocatore umano
    private void eseguiAzioneDichiaraUno() {
        Giocatore corrente = giocatori.get(indiceGiocatoreCorrente);
        if (corrente instanceof GiocatoreUmano) {
            if (corrente.getNumeroCarte() == 2 && corrente.haMosseGiocabili(getCartaInCima(), getColoreAttuale())) {
                corrente.haDichiaratoUnoSuccesso = true;
                pannelloTavolo.aggiungiLog(
                        corrente.nome + " ha dichiarato UNO!");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Puoi dichiarare UNO solo quando hai 2 carte in mano e almeno una giocabile!",
                        "Attenzione", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Non è il tuo turno!", "Attenzione", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Gestisce la contestazione di UNO contro avversari che possiedono 1 carta
    private void eseguiAzioneContestaUno() {
        boolean contestazioneEffettuata = false;
        for (Giocatore g : giocatori) {
            if (g.getNumeroCarte() == 1 && !g.haDichiaratoUnoSuccesso) {
                JOptionPane.showMessageDialog(this,
                        "Contestazione riuscita su " + g.nome + ". Pesca 2 carte.",
                        "Contestazione Riuscita", JOptionPane.WARNING_MESSAGE);
                g.aggiungiCarta(pescaCartaMazzo());
                g.aggiungiCarta(pescaCartaMazzo());
                contestazioneEffettuata = true;
            }
        }
        if (!contestazioneEffettuata) {
            JOptionPane.showMessageDialog(this, "Nessun avversario ha 1 sola carta non dichiarata al momento.",
                    "Contestazione Fallita", JOptionPane.WARNING_MESSAGE);
        }
        aggiornaStatoGrafico();
    }

    // Gestisce il passaggio del turno dopo aver pescato
    private void eseguiAzioneSalta() {
        Giocatore corrente = giocatori.get(indiceGiocatoreCorrente);
        if (!(corrente instanceof GiocatoreUmano)) {
            JOptionPane.showMessageDialog(this, "Attendi, è il turno di " + corrente.nome + "!", "Turno in Corso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (varianteStacking && accumuloPesca > 0) {
            JOptionPane.showMessageDialog(this,
                    "Non puoi passare il turno durante lo stacking! Devi giocare un +2/+4 o pescare.",
                    "Stacking Attivo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!haPescatoNelTurno) {
            JOptionPane.showMessageDialog(this,
                    "Devi prima pescare una carta dal mazzo per poter passare il turno!",
                    "Azione Non Valida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        passaAlProssimoGiocatore();
        aggiornaStatoGrafico();
    }

    private class AzioniPannelloListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String comando = e.getActionCommand();
            switch (comando) {
                case "PESCA" -> eseguiAzionePesca();
                case "SALTA" -> eseguiAzioneSalta();
                case "DICHIARA_UNO" -> eseguiAzioneDichiaraUno();
                case "CONTESTA_UNO" -> eseguiAzioneContestaUno();
                case "NUOVA_PARTITA" -> apriConfigurazionePartita();
                case "PUNTEGGI" -> {
                    SchermataPunteggi punteggi = new SchermataPunteggi(FinestraPrincipale.this, mappaPunteggio,
                            sogliaVittoria, null, false);
                    punteggi.setVisible(true);
                }
            }
        }
    }

    private class CartaManoListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof PannelloCarta iconaCarta) {
                Carta cartaScelta = iconaCarta.getCarta();
                Giocatore corrente = giocatori.get(indiceGiocatoreCorrente);
                boolean statoTurnoBot = !(corrente instanceof GiocatoreUmano);
                if (statoTurnoBot) {
                    JOptionPane.showMessageDialog(FinestraPrincipale.this,
                            "Attendi, è il turno di " + corrente.nome + "!",
                            "Turno in Corso", JOptionPane.WARNING_MESSAGE);
                } else {
                    eseguiMossaGiocatore(corrente, cartaScelta);
                }
            }
        }
    }

    // Esegue la mossa selezionata dal giocatore umano e applica regole/varianti
    private void eseguiMossaGiocatore(Giocatore corrente, Carta cartaScelta) {
        Carta cartaInCima = getCartaInCima();
        Colore coloreAttuale = getColoreAttuale();
        if (varianteStacking && accumuloPesca > 0) {
            if (cartaScelta.getValoreCarta() != Valore.DRAW_TWO
                    && cartaScelta.getValoreCarta() != Valore.WILD_DRAW_FOUR) {
                JOptionPane.showMessageDialog(this,
                        "Puoi giocare solo un +2 o un +4, oppure pescare.", "Mossa Illegale",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        if (corrente.verificaMossa(cartaScelta, cartaInCima, coloreAttuale)) {

            colorePrecedente = coloreAttuale;

            if (varianteNumberRush && checkCarteNumericheUguali(corrente, cartaScelta)) {
                List<Carta> uguali = corrente.getMano().stream()
                        .filter(carta -> carta.getValoreCarta() == cartaScelta.getValoreCarta())
                        .toList();
                List<Carta> carteGiocate = new LinkedList<>();
                for (Carta carta : uguali) {
                    corrente.giocaCarta(carta);
                    scarti.add(carta);
                    carteGiocate.add(carta);
                }
                pannelloTavolo.aggiungiLog("NumberRush.\n" + corrente.nome + " ha giocato: " + carteGiocate);
                if (cartaScelta.getColoreCarta() == Colore.NESSUNO) {
                    Colore nuovoColore = ScegliColore.mostraColore(this);
                    setScartiColore(nuovoColore);
                }
                for (int i = 0; i < uguali.size(); i++) {
                    if (i > 0 && (cartaScelta.getValoreCarta() == Valore.ZERO
                            || cartaScelta.getValoreCarta() == Valore.SETTE)) {
                        continue;
                    }

                    applicaEffettiRegole(cartaScelta, corrente);
                }
            } else {
                corrente.giocaCarta(cartaScelta);
                scarti.add(cartaScelta);
                if (cartaScelta.getColoreCarta() == Colore.NESSUNO) {
                    Colore nuovoColore = ScegliColore.mostraColore(this);
                    setScartiColore(nuovoColore);
                }

                applicaEffettiRegole(cartaScelta, corrente);
            }
            if (corrente.getNumeroCarte() == 1) {
                if (!corrente.haDichiaratoUnoSuccesso) {
                    for (Giocatore g : giocatori) {
                        if (!g.equals(corrente) && !(g instanceof GiocatoreUmano) && g.contestazioneUno(corrente)) {
                            JOptionPane.showMessageDialog(this,
                                    g.nome + " contesta la mancata dichiarazione di UNO! Peschi 2 carte.",
                                    "Contestazione Subita!", JOptionPane.WARNING_MESSAGE);
                            corrente.aggiungiCarta(pescaCartaMazzo());
                            corrente.aggiungiCarta(pescaCartaMazzo());
                            break;
                        }
                    }
                }
            }
            if (corrente.getNumeroCarte() == 0) {
                gestisciFineRound(corrente);
                return;
            }

            passaAlProssimoGiocatore();
            aggiornaStatoGrafico();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Mossa non valida!\nDevi giocare una carta del colore corrente (" + coloreAttuale.getNome() +
                            ") o del valore (" + cartaInCima.getValoreCarta() + ").",
                    "Carta Non Giocabile", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Esegue il turno automatico per un bot
    private void eseguiMossaBot() {
        if (partitaFinita)
            return;

        Giocatore corrente = giocatori.get(indiceGiocatoreCorrente);
        if (corrente.getNumeroCarte() == 0)
            gestisciFineRound(corrente);
        if (corrente instanceof GiocatoreUmano) {
            return;
        }

        Carta cartaInCima = getCartaInCima();
        Colore coloreAttuale = getColoreAttuale();
        Giocatore prossimo = giocatori.get((indiceGiocatoreCorrente + direzione + giocatori.size()) % giocatori.size());

        // Stacking attivo per Bot
        if (varianteStacking && accumuloPesca > 0) {
            Carta cartaBot = corrente.getMano().stream()
                    .filter(carta -> (carta.getValoreCarta() == Valore.DRAW_TWO
                            || carta.getValoreCarta() == Valore.WILD_DRAW_FOUR)
                            && corrente.verificaMossa(carta, cartaInCima, coloreAttuale))
                    .findFirst()
                    .orElse(null);

            if (cartaBot != null) {
                colorePrecedente = coloreAttuale;
                corrente.giocaCarta(cartaBot);
                scarti.add(cartaBot);
                if (cartaBot.getColoreCarta() == Colore.NESSUNO) {
                    setScartiColore(corrente.scegliColore());
                }
                applicaEffettiRegole(cartaBot, corrente);
            } else {
                int pescati = accumuloPesca;
                for (int i = 0; i < pescati; i++) {
                    corrente.aggiungiCarta(pescaCartaMazzo());
                }
                accumuloPesca = 0;
                corrente.haDichiaratoUnoSuccesso = false;
                pannelloTavolo.aggiungiLog(
                        corrente.nome + " non risponde allo stacking e pesca " + pescati + " carte e salta il turno.");
            }
            if (corrente.getNumeroCarte() == 0) {
                gestisciFineRound(corrente);
                return;
            }
            passaAlProssimoGiocatore();
            aggiornaStatoGrafico();
            return;
        }

        Carta mossa = corrente.scegliMossa(cartaInCima, coloreAttuale, prossimo.nome);
        if (mossa != null && corrente.verificaMossa(mossa, cartaInCima, coloreAttuale)) {

            colorePrecedente = coloreAttuale;

            if (varianteNumberRush && checkCarteNumericheUguali(corrente, mossa)) {
                List<Carta> uguali = corrente.getMano().stream()
                        .filter(carta -> carta.getValoreCarta() == mossa.getValoreCarta())
                        .toList();
                List<Carta> carteGiocate = new LinkedList<>();
                for (Carta carta : uguali) {
                    corrente.giocaCarta(carta);
                    scarti.add(carta);
                    carteGiocate.add(carta);
                }
                pannelloTavolo.aggiungiLog("NumberRush.\n" + corrente.nome + " ha giocato: " + carteGiocate);
                if (mossa.getColoreCarta() == Colore.NESSUNO) {
                    setScartiColore(corrente.scegliColore());
                }
                for (int i = 0; i < uguali.size(); i++) {
                    if (i > 0 && (mossa.getValoreCarta() == Valore.ZERO || mossa.getValoreCarta() == Valore.SETTE)) {
                        continue;
                    }
                    applicaEffettiRegole(mossa, corrente);
                }
            } else {
                corrente.giocaCarta(mossa);
                scarti.add(mossa);
                if (mossa.getColoreCarta() == Colore.NESSUNO) {
                    setScartiColore(corrente.scegliColore());
                }
                applicaEffettiRegole(mossa, corrente);
            }

            if (corrente.getNumeroCarte() == 1) {
                if (corrente.haDichiaratoUno()) {
                    corrente.haDichiaratoUnoSuccesso = true;
                    pannelloTavolo.aggiungiLog(corrente.nome + " ha dichiarato UNO!");
                } else {
                    corrente.haDichiaratoUnoSuccesso = false;
                    for (Giocatore g : giocatori) {
                        if (!g.equals(corrente) && !(g instanceof GiocatoreUmano)
                                && g.contestazioneUno(corrente)) {
                            JOptionPane.showMessageDialog(this,
                                    g.nome + " contesta UNO a " + corrente.nome + "! Pesca 2 carte!",
                                    "Contestazione Riuscita", JOptionPane.WARNING_MESSAGE);
                            corrente.aggiungiCarta(pescaCartaMazzo());
                            corrente.aggiungiCarta(pescaCartaMazzo());
                            break;
                        }
                    }
                }
            }
        } else {
            Carta pescata = pescaCartaMazzo();
            corrente.aggiungiCarta(pescata);
            corrente.haDichiaratoUnoSuccesso = false;
            pannelloTavolo.aggiungiLog(corrente.nome + " ha pescato una carta.");
            if (corrente.verificaMossa(pescata, cartaInCima, coloreAttuale)) {
                colorePrecedente = coloreAttuale;
                corrente.giocaCarta(pescata);
                scarti.add(pescata);
                if (pescata.getColoreCarta() == Colore.NESSUNO) {
                    setScartiColore(corrente.scegliColore());
                }
                applicaEffettiRegole(pescata, corrente);
            }

        }

        if (corrente.getNumeroCarte() == 0) {
            gestisciFineRound(corrente);
            return;
        }

        passaAlProssimoGiocatore();
        aggiornaStatoGrafico();
    }

    // Applica gli effetti delle carte speciali e delle varianti attive
    private void applicaEffettiRegole(Carta carta, Giocatore corrente) {
        if (corrente.getNumeroCarte() == 0) {
            return;
        }
        Valore valore = carta.getValoreCarta();

        switch (valore) {
            case SKIP -> {
                passaAlProssimoGiocatore();
            }
            case REVERSE -> {
                direzione *= -1;
                if (giocatori.size() == 2) {
                    passaAlProssimoGiocatore();
                }
            }
            case DRAW_TWO -> {
                if (varianteStacking) {
                    accumuloPesca += 2;
                } else {
                    passaAlProssimoGiocatore();
                    Giocatore bersaglio = giocatori.get(indiceGiocatoreCorrente);
                    bersaglio.aggiungiCarta(pescaCartaMazzo());
                    bersaglio.aggiungiCarta(pescaCartaMazzo());
                    bersaglio.haDichiaratoUnoSuccesso = false;
                    pannelloTavolo.aggiungiLog(corrente.nome + " ha giocato un +2!\n" + bersaglio.nome
                            + " pesca 2 carte e salta il turno.");
                }
            }
            case WILD_DRAW_FOUR -> {
                if (varianteStacking) {
                    accumuloPesca += 4;
                } else {
                    passaAlProssimoGiocatore();
                    Giocatore bersaglio = giocatori.get(indiceGiocatoreCorrente);
                    boolean challengeAttivata = false;
                    if (bersaglio instanceof GiocatoreUmano) {
                        int risposta = JOptionPane.showConfirmDialog(this,
                                corrente.nome
                                        + " ha giocato un +4!\nVuoi fare la Challenge?",
                                "Challenge +4", JOptionPane.YES_NO_OPTION);
                        challengeAttivata = (risposta == JOptionPane.YES_OPTION);
                    } else {
                        challengeAttivata = bersaglio.effettuaChallenge(corrente.getNumeroCarte());
                        if (challengeAttivata) {
                            JOptionPane.showMessageDialog(this, bersaglio.nome
                                    + " lancia una Challenge sul +4 giocato da " + corrente.nome + "!", "Challenge",
                                    JOptionPane.WARNING_MESSAGE);
                        }
                    }

                    if (challengeAttivata) {
                        boolean haColore = false;
                        if (colorePrecedente != Colore.NESSUNO) {
                            haColore = corrente.haCartaDiColore(colorePrecedente);
                        }

                        if (haColore) {
                            pannelloTavolo.aggiungiLog(bersaglio.nome + " ha vinto la Challenge!\n" + corrente.nome
                                    + " aveva il colore e pesca 4 carte.");
                            for (int i = 0; i < 4; i++)
                                corrente.aggiungiCarta(pescaCartaMazzo());
                            corrente.haDichiaratoUnoSuccesso = false;
                            indiceGiocatoreCorrente = (indiceGiocatoreCorrente - direzione + giocatori.size())
                                    % giocatori.size();

                        } else {
                            JOptionPane.showMessageDialog(this,
                                    bersaglio.nome + " ha perso la Challenge!\n" + corrente.nome
                                            + " non aveva carte del colore.\n" + bersaglio.nome
                                            + " pesca 6 carte e salta il turno!",
                                    "Challenge Fallita", JOptionPane.ERROR_MESSAGE);
                            for (int i = 0; i < 6; i++)
                                bersaglio.aggiungiCarta(pescaCartaMazzo());
                            bersaglio.haDichiaratoUnoSuccesso = false;
                        }
                    } else {
                        for (int i = 0; i < 4; i++)
                            bersaglio.aggiungiCarta(pescaCartaMazzo());
                        bersaglio.haDichiaratoUnoSuccesso = false;
                        pannelloTavolo.aggiungiLog(corrente.nome + " ha giocato un +4!\n" + bersaglio.nome
                                + " pesca 4 carte e salta il turno.");
                    }
                }
            }
            case ZERO -> {
                if (varianteSevenZero) {
                    List<List<Carta>> listaMani = new ArrayList<>();
                    for (Giocatore g : giocatori) {
                        listaMani.add(new ArrayList<>(g.getMano()));
                    }
                    for (int i = 0; i < giocatori.size(); i++) {
                        int posizione = (i - direzione + giocatori.size()) % giocatori.size();
                        Giocatore bersaglio = giocatori.get(i);
                        bersaglio.getMano().clear();
                        bersaglio.getMano().addAll(listaMani.get(posizione));
                    }
                    pannelloTavolo.aggiungiLog("Regola dello 0: scambio di mani tra giocatori.");
                }
            }
            case SETTE -> {
                if (varianteSevenZero) {
                    Giocatore bersaglio;
                    if (corrente instanceof GiocatoreUmano) {
                        bersaglio = ScegliGiocatore.mostraGiocatore(this, corrente, giocatori);
                    } else {
                        bersaglio = corrente.scegliGiocatoreDaScambiare(corrente, giocatori);
                    }

                    if (bersaglio != null && !bersaglio.equals(corrente)) {
                        List<Carta> manoTemporanea = new ArrayList<>(corrente.getMano());
                        corrente.getMano().clear();
                        corrente.getMano().addAll(bersaglio.getMano());
                        bersaglio.getMano().clear();
                        bersaglio.getMano().addAll(manoTemporanea);
                        pannelloTavolo.aggiungiLog(
                                "Regola del 7: " + corrente.nome + " ha scambiato la mano con " + bersaglio.nome + "!");
                    }
                }
            }
            default -> {
            }
        }
    }

    private boolean checkCarteNumericheUguali(Giocatore corrente, Carta cartaScelta) {
        if (cartaScelta.getValoreCarta() == getCartaInCima().getValoreCarta()) {

            if (cartaScelta.getValoreCarta().ordinal() > Valore.NOVE.ordinal()) {
                return false;
            }
            return corrente.getMano().stream()
                    .filter(carta -> !carta.equals(cartaScelta))
                    .anyMatch(carta -> carta.getValoreCarta() == cartaScelta.getValoreCarta());
        } else {
            return false;
        }
    }

    // Gestisce la fine del round, assegna i punti e apre la schermata punteggi
    private void gestisciFineRound(Giocatore vincitoreRound) {
        if (timerTurnoBot != null)
            timerTurnoBot.stop();
        if (!modalitaPunti) {
            pannelloTavolo.aggiungiLog("COMPLIMENTI! " + vincitoreRound.nome + " ha vinto la partita.");
            partitaFinita = true;
            aggiornaStatoGrafico();
            return;
        }

        int puntiRound = 0;
        for (Giocatore giocatore : giocatori) {
            if (!giocatore.equals(vincitoreRound)) {
                puntiRound += giocatore.calcolaPuntiMano();
            }
        }
        pannelloTavolo.aggiungiLog(vincitoreRound.nome + " ha vinto il round!");
        int puntiPrecedenti = mappaPunteggio.getOrDefault(vincitoreRound, 0);
        int puntiTotali = puntiPrecedenti + puntiRound;
        mappaPunteggio.put(vincitoreRound, puntiTotali);

        boolean raggiuntoSoglia = false;
        if (puntiTotali >= sogliaVittoria)
            raggiuntoSoglia = true;

        SchermataPunteggi punteggi = new SchermataPunteggi(this, mappaPunteggio, sogliaVittoria, vincitoreRound,
                raggiuntoSoglia);
        punteggi.setVisible(true);

        if (raggiuntoSoglia) {
            partitaFinita = true;
            pannelloTavolo.aggiungiLog(
                    vincitoreRound.nome + " ha superato la soglia di " + sogliaVittoria + " punti e vince.");
            aggiornaStatoGrafico();
        } else {
            nuovoRound();
        }

    }

    // Passa il turno al giocatore successivo in base alla direzione
    private void passaAlProssimoGiocatore() {
        haPescatoNelTurno = false;
        indiceGiocatoreCorrente = (indiceGiocatoreCorrente + direzione + giocatori.size()) % giocatori.size();
    }

    // Pesca una carta dal mazzo (rimescola gli scarti se esaurito)
    private Carta pescaCartaMazzo() {
        if (mazzoPesca.isEmpty()) {
            if (scarti.size() > 1) {
                Carta cartaInCima = scarti.remove(scarti.size() - 1);
                for (Carta carta : scarti) {
                    if (carta.getValoreCarta() == Valore.WILD || carta.getValoreCarta() == Valore.WILD_DRAW_FOUR) {
                        carta.setColoreCarta(Colore.NESSUNO);
                    }
                }
                mazzoPesca.addAll(scarti);
                scarti.clear();
                scarti.add(cartaInCima);
                Mazzo.mescola(mazzoPesca);
            }
        }
        return mazzoPesca.isEmpty() ? new Carta(Colore.ROSSO, Valore.ZERO) : mazzoPesca.remove(0);
    }

    // Restituisce la carta in cima alla pila degli scarti
    private Carta getCartaInCima() {
        return scarti.isEmpty() ? new Carta(Colore.ROSSO, Valore.ZERO) : scarti.get(scarti.size() - 1);
    }

    // Restituisce il colore attivo di gioco
    private Colore getColoreAttuale() {
        Carta inCima = getCartaInCima();
        return inCima.getColoreCarta() == Colore.NESSUNO ? Colore.ROSSO : inCima.getColoreCarta();
    }

    // Imposta il colore della carta in cima agli scarti
    private void setScartiColore(Colore colore) {
        Carta inCima = getCartaInCima();
        if (inCima != null) {
            inCima.setColoreCarta(colore);
        }
    }

    // Avvia un timer di 2 secondi per simulare il ragionamento del bot
    private void controllaEAvviaTurnoBot() {
        if (partitaFinita)
            return;
        if (timerTurnoBot != null && timerTurnoBot.isRunning()) {
            return;
        }

        Giocatore corrente = giocatori.get(indiceGiocatoreCorrente);
        if (!(corrente instanceof GiocatoreUmano)) {
            timerTurnoBot = new Timer(2000, evento -> {
                if (!partitaFinita && !(giocatori.get(indiceGiocatoreCorrente) instanceof GiocatoreUmano)) {
                    eseguiMossaBot();
                }
            });
            timerTurnoBot.setRepeats(false);
            timerTurnoBot.start();
        }
    }

    // Aggiorna tutti i pannelli dell'interfaccia grafica
    private void aggiornaStatoGrafico() {
        Giocatore corrente = giocatori.get(indiceGiocatoreCorrente);

        pannelloAvversari.aggiornaStato(corrente, giocatori, mappaPunteggio, modalitaPunti);
        pannelloTavolo.aggiornaTavolo(getCartaInCima(), getColoreAttuale(), direzione, accumuloPesca);

        List<Carta> manoDaMostrare;
        if (corrente instanceof GiocatoreUmano) {
            manoDaMostrare = corrente.getMano();
        } else if (giocatoreUmanoPrincipale != null) {
            manoDaMostrare = giocatoreUmanoPrincipale.getMano();
        } else {
            manoDaMostrare = corrente.getMano();
        }
        pannelloManoGiocatore.aggiornaMano(manoDaMostrare);

        pannelloTavolo.paintImmediately(0, 0, pannelloTavolo.getWidth(), pannelloTavolo.getHeight());
        pannelloManoGiocatore.paintImmediately(0, 0, pannelloManoGiocatore.getWidth(),
                pannelloManoGiocatore.getHeight());
        pannelloAvversari.paintImmediately(0, 0, pannelloAvversari.getWidth(), pannelloAvversari.getHeight());

        controllaEAvviaTurnoBot();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FinestraPrincipale finestra = new FinestraPrincipale();
            finestra.setResizable(false);
            finestra.setVisible(true);
        });
    }
}