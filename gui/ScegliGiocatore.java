package gui;

import backend.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Finestra di dialogo per scegliere con quale avversario scambiare la mano
 * (regola del 7).
 */
public class ScegliGiocatore extends JDialog {

    private Giocatore giocatoreScelto;
    private final Map<JButton, Giocatore> mappaPulsanti = new HashMap<>();

    /**
     * Crea la finestra con un pulsante per ciascun avversario.
     * 
     * @param pannelloPrincipale   la finestra principale
     * @param corrente             il giocatore che ha giocato il 7
     * @param giocatoriDisponibili tutti i giocatori della partita
     */
    public ScegliGiocatore(JFrame pannelloPrincipale, Giocatore corrente, List<Giocatore> giocatoriDisponibili) {
        super(pannelloPrincipale, "Scelta mano da scambiare", true);
        this.setLayout(new BorderLayout(10, 10));
        this.setSize(400, 300);
        this.setLocationRelativeTo(pannelloPrincipale);
        this.setResizable(false);

        JLabel informazioni = new JLabel("Hai giocato un 7! Scegli un avversario per scambiare la mano:",
                SwingConstants.CENTER);
        informazioni.setFont(StileUI.caricaFont());
        informazioni.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        this.add(informazioni, BorderLayout.NORTH);

        JPanel pannelloBottoni = new JPanel(new GridLayout(0, 1, 8, 8));
        pannelloBottoni.setBorder(BorderFactory.createEmptyBorder(0, 20, 15, 20));

        for (Giocatore g : giocatoriDisponibili) {
            if (!g.equals(corrente)) {
                JButton bottone = new JButton(g.nome + " (" + g.getNumeroCarte() + " carte)");
                bottone.setFont(StileUI.caricaFont());
                bottone.setBackground(new Color(38, 166, 154));
                bottone.setForeground(Color.WHITE);
                bottone.setOpaque(true);
                bottone.setContentAreaFilled(true);
                bottone.setBorderPainted(false);
                bottone.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        JButton source = (JButton) e.getSource();
                        giocatoreScelto = mappaPulsanti.get(source);
                        ScegliGiocatore.this.dispose();
                    }
                });
                bottone.setFocusPainted(false);
                mappaPulsanti.put(bottone, g);
                pannelloBottoni.add(bottone);
            }
        }

        this.getContentPane().add(pannelloBottoni, BorderLayout.CENTER);
    }

    /**
     * Restituisce l'avversario selezionato per lo scambio.
     * 
     * @return il giocatore scelto
     */
    public Giocatore getGiocatoreScelto() {
        return giocatoreScelto;
    }

    /**
     * Mostra la finestra di dialogo e restituisce il giocatore selezionato.
     * 
     * @param pannelloPrincipale   la finestra principale
     * @param corrente             il giocatore attivo
     * @param giocatoriDisponibili tutti i giocatori
     * @return il giocatore con cui scambiare le carte
     */
    public static Giocatore mostraGiocatore(JFrame pannelloPrincipale, Giocatore corrente,
            List<Giocatore> giocatoriDisponibili) {
        ScegliGiocatore sceltaGiocatore = new ScegliGiocatore(pannelloPrincipale, corrente, giocatoriDisponibili);
        sceltaGiocatore.setVisible(true);
        return sceltaGiocatore.getGiocatoreScelto();
    }
}
