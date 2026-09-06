package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Pannello laterale con i pulsanti di controllo (Dichiara UNO, Contesta UNO,
 * Punteggi, Nuova Partita, Salta).
 */
public class PannelloAzioni extends JPanel {

    private final JButton bottoneDichiaraUno;
    private final JButton bottoneContestaUno;
    private final JButton bottoneNuovaPartita;
    private final JButton bottonePunteggi;
    private final JButton bottoneSalta;

    /**
     * Crea il pannello laterale posizionando i pulsanti in verticale.
     */
    public PannelloAzioni() {
        this.setLayout(new GridBagLayout());
        this.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(7, 15, 7, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        bottoneDichiaraUno = creaPulsante("DICHIARA UNO!", new Color(13, 133, 26));
        bottoneDichiaraUno.setActionCommand("DICHIARA_UNO");

        bottoneContestaUno = creaPulsante("CONTESTA UNO!", new Color(145, 4, 4));
        bottoneContestaUno.setActionCommand("CONTESTA_UNO");

        bottoneSalta = creaPulsante("SALTA", new Color(92, 25, 25));
        bottoneSalta.setActionCommand("SALTA");

        bottonePunteggi = creaPulsante("PUNTEGGI", new Color(7, 187, 219));
        bottonePunteggi.setActionCommand("PUNTEGGI");

        bottoneNuovaPartita = creaPulsante("NUOVA PARTITA", new Color(61, 58, 58));
        bottoneNuovaPartita.setActionCommand("NUOVA_PARTITA");

        gbc.gridy = 0;
        this.add(bottoneDichiaraUno, gbc);
        gbc.gridy = 1;
        this.add(bottoneContestaUno, gbc);
        gbc.gridy = 2;
        this.add(bottonePunteggi, gbc);
        gbc.gridy = 3;
        this.add(bottoneNuovaPartita, gbc);
        gbc.gridy = 4;
        this.add(bottoneSalta, gbc);
    }

    /**
     * Collega un ActionListener a tutti i pulsanti del pannello.
     * 
     * @param listener il gestore degli eventi
     */
    public void registraActionListener(ActionListener listener) {
        bottoneDichiaraUno.addActionListener(listener);
        bottoneContestaUno.addActionListener(listener);
        bottonePunteggi.addActionListener(listener);
        bottoneNuovaPartita.addActionListener(listener);
        bottoneSalta.addActionListener(listener);
    }

    /**
     * Crea e personalizza graficamente un pulsante.
     * 
     * @param testo        il testo del pulsante
     * @param coloreSfondo il colore dello sfondo
     * @return il pulsante creato
     */
    private JButton creaPulsante(String testo, Color coloreSfondo) {
        JButton bottone = new JButton(testo);
        bottone.setPreferredSize(new Dimension(220, 42));
        bottone.setBackground(coloreSfondo);
        bottone.setOpaque(true);
        bottone.setContentAreaFilled(true);
        bottone.setBorderPainted(false);
        bottone.setForeground(Color.WHITE);
        bottone.setFont(StileUI.caricaFont());
        bottone.setFocusPainted(false);
        bottone.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return bottone;
    }

    /**
     * Restituisce il pulsante "Dichiara UNO".
     *
     * @return il pulsante per dichiarare UNO
     */
    public JButton getBottoneDichiaraUno() {
        return bottoneDichiaraUno;
    }

    /**
     * Restituisce il pulsante "Contesta UNO".
     *
     * @return il pulsante per contestare UNO
     */
    public JButton getBottoneContestaUno() {
        return bottoneContestaUno;
    }

    /**
     * Restituisce il pulsante "Nuova Partita".
     *
     * @return il pulsante per avviare una nuova partita
     */
    public JButton getBottoneNuovaPartita() {
        return bottoneNuovaPartita;
    }

    /**
     * Restituisce il pulsante "Punteggi".
     *
     * @return il pulsante per visualizzare i punteggi
     */
    public JButton getBottonePunteggi() {
        return bottonePunteggi;
    }

    /**
     * Restituisce il pulsante "Salta".
     *
     * @return il pulsante per saltare il turno
     */
    public JButton getBottoneSalta() {
        return bottoneSalta;
    }
}
