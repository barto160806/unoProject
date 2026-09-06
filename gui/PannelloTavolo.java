package gui;

import backend.*;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Pannello centrale del tavolo di gioco.
 * Mostra il verso di gioco, il colore corrente, la carta in cima agli scarti,
 * il mazzo di pesca, i log delle azioni e l'accumulo dello stacking.
 */
public class PannelloTavolo extends JPanel {

    private static final Color coloreSfondo = new Color(27, 94, 32);
    private static final Color colorePannello = new Color(18, 69, 23);
    private final JLabel COLOREATTUALE;
    private final JLabel valoreColore;
    private final JLabel versoDiGioco;
    private final JLabel stacking;
    private final JTextArea logs;
    private final JScrollPane contenitoreLogs;
    private final JPanel contenitoreScarti;
    private final JPanel contenitoreMazzo;
    private PannelloCarta widgetScarti;
    private PannelloCarta widgetMazzo;
    private ActionListener ascoltatoreMazzo;

    /**
     * Crea il layout del tavolo da gioco.
     */
    public PannelloTavolo() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBackground(coloreSfondo);

        // Pannello NORD: versoDiGioco e coloreAttuale
        JPanel panelloNord = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panelloNord.setOpaque(false);
        versoDiGioco = new JLabel("Verso: Orario", SwingConstants.CENTER);
        versoDiGioco.setFont(StileUI.caricaFont());
        versoDiGioco.setForeground(new Color(255, 215, 0));
        versoDiGioco.setBackground(colorePannello);
        versoDiGioco.setOpaque(true);

        COLOREATTUALE = new JLabel("Colore Attuale: ", SwingConstants.CENTER);
        COLOREATTUALE.setFont(StileUI.caricaFont());
        COLOREATTUALE.setForeground(new Color(255, 215, 0));
        COLOREATTUALE.setBackground(colorePannello);
        COLOREATTUALE.setOpaque(true);

        valoreColore = new JLabel("", SwingConstants.CENTER);
        valoreColore.setFont(StileUI.caricaFont());
        valoreColore.setOpaque(true);

        panelloNord.add(versoDiGioco);
        panelloNord.add(COLOREATTUALE);
        panelloNord.add(valoreColore);
        this.add(panelloNord, BorderLayout.NORTH);

        // Pannello Centrale: mazzo,scarti e logs
        JPanel pannelloCentrale = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 40));
        pannelloCentrale.setOpaque(false);

        JPanel pannelloMazzo = new JPanel(new BorderLayout(5, 5));
        pannelloMazzo.setOpaque(false);
        JLabel testoMazzo = new JLabel("MAZZO", SwingConstants.CENTER);
        testoMazzo.setForeground(Color.WHITE);
        testoMazzo.setBackground(colorePannello);
        testoMazzo.setOpaque(true);
        testoMazzo.setFont(StileUI.caricaFont());

        contenitoreMazzo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        contenitoreMazzo.setOpaque(false);

        pannelloMazzo.add(testoMazzo, BorderLayout.NORTH);
        pannelloMazzo.add(contenitoreMazzo, BorderLayout.CENTER);

        JPanel pannelloScarti = new JPanel(new BorderLayout(15, 8));
        pannelloScarti.setOpaque(false);
        JLabel testoScarti = new JLabel("SCARTI", SwingConstants.CENTER);
        testoScarti.setForeground(Color.WHITE);
        testoScarti.setFont(StileUI.caricaFont());
        testoScarti.setBackground(colorePannello);
        testoScarti.setOpaque(true);

        contenitoreScarti = new JPanel(new FlowLayout(FlowLayout.CENTER));
        contenitoreScarti.setOpaque(false);

        pannelloScarti.add(testoScarti, BorderLayout.NORTH);
        pannelloScarti.add(contenitoreScarti, BorderLayout.CENTER);

        JPanel pannelloLogs = new JPanel(new BorderLayout(15, 8));
        pannelloLogs.setOpaque(false);
        JLabel testoLogs = new JLabel("Logs");
        testoLogs.setForeground(new Color(255, 215, 0));
        testoLogs.setFont(StileUI.fredokaFont);
        testoLogs.setBackground(colorePannello);
        testoLogs.setOpaque(true);

        logs = new JTextArea();
        logs.setFont(StileUI.fredokaFont);
        logs.setForeground(Color.WHITE);
        logs.setEditable(false);
        logs.setLineWrap(true);
        logs.setWrapStyleWord(true);
        logs.setOpaque(false);

        contenitoreLogs = new JScrollPane(logs);
        contenitoreLogs.setBorder(BorderFactory.createEmptyBorder());
        contenitoreLogs.getViewport().setOpaque(false);
        contenitoreLogs.setOpaque(false);
        contenitoreLogs.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        contenitoreLogs.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        contenitoreLogs.setPreferredSize(new Dimension(230, 150));

        pannelloLogs.add(testoLogs, BorderLayout.NORTH);
        pannelloLogs.add(contenitoreLogs, BorderLayout.CENTER);
        this.add(pannelloLogs, BorderLayout.WEST);

        JPanel spaziatoreEst = new JPanel();
        spaziatoreEst.setOpaque(false);
        spaziatoreEst.setPreferredSize(new Dimension(230, 150));
        this.add(spaziatoreEst, BorderLayout.EAST);

        pannelloCentrale.add(pannelloMazzo);
        pannelloCentrale.add(pannelloScarti);

        this.add(pannelloCentrale, BorderLayout.CENTER);

        // Pannello SUD per lo stacking
        JPanel pannelloSud = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pannelloSud.setOpaque(false);

        stacking = new JLabel("", SwingConstants.CENTER);
        stacking.setFont(StileUI.caricaFont());
        stacking.setForeground(Color.WHITE);
        stacking.setBackground(new Color(242, 105, 0));
        stacking.setOpaque(true);
        pannelloSud.add(stacking);
        this.add(pannelloSud, BorderLayout.SOUTH);

        aggiornaMazzo(new PannelloCarta(null));
    }

    /**
     * Aggiorna gli elementi del tavolo (scarti, colore, verso e accumulo pesca).
     * 
     * @param cartaInCima   la carta in cima agli scarti
     * @param coloreAttuale il colore attivo
     * @param direzione     1 per senso orario, -1 per antiorario
     * @param accumuloPesca carte accumulate nello stacking (+2/+4)
     */
    public void aggiornaTavolo(Carta cartaInCima, Colore coloreAttuale, int direzione, int accumuloPesca) {
        contenitoreScarti.removeAll();
        widgetScarti = new PannelloCarta(cartaInCima);
        contenitoreScarti.add(widgetScarti, BorderLayout.CENTER);
        contenitoreScarti.revalidate();
        contenitoreScarti.repaint();

        if (coloreAttuale != null) {
            valoreColore.setText(coloreAttuale.getNome().toUpperCase());
            valoreColore.setBackground(convertiColore(coloreAttuale));
            valoreColore.setForeground(Color.WHITE);
        }

        versoDiGioco.setText(direzione == 1 ? "Verso: Orario" : "Verso: Antiorario");

        if (accumuloPesca > 0) {
            stacking.setText("ACCUMULO PESCA: +" + accumuloPesca + " CARTE!");
        } else {
            stacking.setText("");
        }
    }

    /**
     * Aggiorna il componente grafico del mazzo di pesca.
     * 
     * @param widgetMazzo la carta coperta del mazzo
     */
    public void aggiornaMazzo(PannelloCarta widgetMazzo) {
        this.widgetMazzo = widgetMazzo;
        if (widgetMazzo != null && ascoltatoreMazzo != null) {
            widgetMazzo.setActionCommand("PESCA");
            widgetMazzo.addActionListener(ascoltatoreMazzo);
        }
        contenitoreMazzo.removeAll();
        contenitoreMazzo.add(widgetMazzo, BorderLayout.CENTER);
        contenitoreMazzo.revalidate();
        contenitoreMazzo.repaint();
    }

    /**
     * Registra l'ascoltatore per l'azione di pesca cliccando sul mazzo.
     * 
     * @param listener il gestore degli eventi
     */
    public void registraMazzoListener(ActionListener listener) {
        this.ascoltatoreMazzo = listener;
        if (widgetMazzo != null) {
            widgetMazzo.setActionCommand("PESCA");
            widgetMazzo.addActionListener(listener);
        }
    }

    /**
     * Restituisce il componente grafico del mazzo di pesca.
     *
     * @return il widget del mazzo
     */
    public PannelloCarta getWidgetMazzo() {
        return widgetMazzo;
    }

    /**
     * Aggiunge un messaggio nel riquadro dei log di gioco.
     * 
     * @param messaggio il testo del log
     */
    public void aggiungiLog(String messaggio) {
        try {
            int inizio = logs.getDocument().getLength();
            logs.append(messaggio + "\n");
            int fine = logs.getDocument().getLength() - 1;

            javax.swing.text.Highlighter highlighter = logs.getHighlighter();
            highlighter.removeAllHighlights();
            highlighter.addHighlight(inizio, fine,
                    new javax.swing.text.DefaultHighlighter.DefaultHighlightPainter(new Color(161, 127, 5)));

            logs.setCaretPosition(fine);
        } catch (Exception e) {
            logs.setCaretPosition(logs.getDocument().getLength());
        }
    }

    /**
     * Pulisce tutti i messaggi presenti nel riquadro dei log.
     */
    public void pulisciLogs() {
        logs.setText("");
        if (logs.getHighlighter() != null) {
            logs.getHighlighter().removeAllHighlights();
        }
    }

    /**
     * Converte l'enum Colore nel corrispettivo Color di Java Swing.
     * 
     * @param colore il colore UNO
     * @return il colore grafico
     */
    private Color convertiColore(Colore colore) {
        if (colore == null)
            return Color.GRAY;
        return switch (colore) {
            case ROSSO -> new Color(153, 20, 20);
            case GIALLO -> new Color(185, 125, 0);
            case VERDE -> new Color(46, 125, 50);
            case BLU -> new Color(13, 71, 161);
            case NESSUNO -> Color.DARK_GRAY;
        };
    }
}
