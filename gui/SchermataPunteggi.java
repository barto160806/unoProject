package gui;

import backend.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

/**
 * Finestra di dialogo che mostra la classifica dei punteggi a fine round o a fine partita.
 */
public class SchermataPunteggi extends JDialog {

    /**
     * Crea la schermata dei punteggi con tabella ordinata per punteggio.
     * 
     * @param owner          la finestra principale
     * @param mappaPunteggi  i punteggi dei giocatori
     * @param soglia         la soglia di punti per vincere
     * @param vincitoreRound il vincitore del round
     * @param finePartita    true se la partita e' terminata
     */
    public SchermataPunteggi(Frame owner, Map<Giocatore, Integer> mappaPunteggi, int soglia,
            Giocatore vincitoreRound, boolean finePartita) {
        super(owner, finePartita ? "Fine partita" : "Fine round", true);
        this.setLayout(new BorderLayout(15, 15));
        this.setSize(460, 360);
        this.setLocationRelativeTo(owner);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel pannelloPrincipale = new JPanel(new BorderLayout(10, 10));
        pannelloPrincipale.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titolo = new JLabel(
                finePartita ? "CLASSIFICA FINALE!" : "Punteggi Aggiornati (Soglia: " + soglia + " punti)",
                SwingConstants.CENTER);
        titolo.setFont(StileUI.caricaFont());
        pannelloPrincipale.add(titolo, BorderLayout.NORTH);

        // Tabella Classifica
        String[] colonne = { "Posizione", "Giocatore", "Punti Totali" };
        DefaultTableModel modelloTabella = new DefaultTableModel(colonne, 0) {
            @Override
            public boolean isCellEditable(int righe, int colonne) {
                return false;
            }
        };

        List<Map.Entry<Giocatore, Integer>> listaOrdinata = mappaPunteggi.entrySet().stream()
                .sorted((giocatore1, giocatore2) -> giocatore2.getValue().compareTo(giocatore1.getValue()))
                .toList();

        int pos = 1;
        for (Map.Entry<Giocatore, Integer> coppia : listaOrdinata) {
            Giocatore giocatore = coppia.getKey();
            String nomeGiocatore = giocatore.nome;

            int punti = coppia.getValue();

            String posizione = pos + "°";
            String puntiClassifica = punti + " punti";
            String[] rigaTabella = new String[] { posizione, nomeGiocatore, puntiClassifica };
            modelloTabella.addRow(rigaTabella);
            pos++;
        }

        JTable tabella = new JTable(modelloTabella);
        tabella.setRowHeight(20);
        tabella.setFont(StileUI.caricaFont());
        tabella.getTableHeader().setFont(StileUI.caricaFont());
        pannelloPrincipale.add(new JScrollPane(tabella), BorderLayout.CENTER);

        JButton bottoneContinua = new JButton(finePartita ? "Chiudi" : "Inizia Prossimo Round");
        bottoneContinua.setFont(StileUI.caricaFont());
        bottoneContinua.setBackground(new Color(56, 142, 60));
        bottoneContinua.setForeground(Color.WHITE);
        bottoneContinua.setFocusPainted(false);

        bottoneContinua.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SchermataPunteggi.this.dispose();
            }
        });

        pannelloPrincipale.add(bottoneContinua, BorderLayout.SOUTH);
        this.setContentPane(pannelloPrincipale);
    }
}
