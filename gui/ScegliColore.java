package gui;

import backend.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Finestra di dialogo per la scelta del colore dopo aver giocato un jolly o un +4.
 */
public class ScegliColore extends JDialog {

    private Colore coloreScelto = Colore.ROSSO;

    /**
     * Crea la finestra di dialogo con i 4 pulsanti colorati.
     * 
     * @param framePrincipale la finestra principale
     */
    public ScegliColore(JFrame framePrincipale) {
        super(framePrincipale, "Scegli il nuovo Colore", true);
        this.setLayout(new BorderLayout(10, 10));
        this.setSize(300, 200);
        this.setLocationRelativeTo(framePrincipale);
        this.setResizable(false);
        JPanel pannelloBottoni = new JPanel(new GridLayout(2, 2, 10, 10));
        pannelloBottoni.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

        ColoreListener listenerColore = new ColoreListener();

        JButton bottoneRosso = creaBottoneColore("ROSSO", new Color(211, 47, 47), "ROSSO", listenerColore);
        JButton bottoneGiallo = creaBottoneColore("GIALLO", new Color(251, 192, 45), "GIALLO", listenerColore);
        JButton bottoneVerde = creaBottoneColore("VERDE", new Color(56, 142, 60), "VERDE", listenerColore);
        JButton bottoneBlu = creaBottoneColore("BLU", new Color(25, 118, 210), "BLU", listenerColore);

        pannelloBottoni.add(bottoneRosso);
        pannelloBottoni.add(bottoneGiallo);
        pannelloBottoni.add(bottoneVerde);
        pannelloBottoni.add(bottoneBlu);

        this.getContentPane().add(pannelloBottoni, BorderLayout.CENTER);
    }

    private JButton creaBottoneColore(String testo, Color colore, String comando, ActionListener listener) {
        JButton bottone = new JButton(testo);
        bottone.setBackground(colore);
        bottone.setForeground(Color.WHITE);
        bottone.setFont(StileUI.caricaFont());
        bottone.setActionCommand(comando);
        bottone.addActionListener(listener);
        bottone.setFocusPainted(false);
        return bottone;
    }

    private class ColoreListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String comando = e.getActionCommand();
            if ("GIALLO".equals(comando)) {
                coloreScelto = Colore.GIALLO;
            } else if ("VERDE".equals(comando)) {
                coloreScelto = Colore.VERDE;
            } else if ("BLU".equals(comando)) {
                coloreScelto = Colore.BLU;
            } else {
                coloreScelto = Colore.ROSSO;
            }
            ScegliColore.this.dispose();
        }
    }

    /**
     * Restituisce il colore scelto.
     * 
     * @return il colore selezionato
     */
    public Colore getColoreScelto() {
        return coloreScelto;
    }

    /**
     * Mostra la finestra di dialogo e restituisce il colore scelto.
     * 
     * @param framePrincipale la finestra principale
     * @return il colore selezionato
     */
    public static Colore mostraColore(JFrame framePrincipale) {
        ScegliColore scegliColore = new ScegliColore(framePrincipale);
        scegliColore.setVisible(true);
        return scegliColore.getColoreScelto();
    }
}
