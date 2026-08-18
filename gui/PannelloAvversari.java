package gui;

import backend.*;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pannello superiore che mostra il turno corrente e la lista dei giocatori
 * con il rispettivo numero di carte e punteggio.
 */
public class PannelloAvversari extends JPanel {

    private final JLabel turnoCorrente = new JLabel("Turno di: ");
    private final JPanel pannelloGiocatori = new JPanel();
    private static final Color COLORE_PANNELLO = new Color(18, 69, 23);
    private final Map<Giocatore, JLabel> etichetteGiocatori = new HashMap<>();
    private final JScrollPane pannelloScorrimento;

    /**
     * Crea il pannello superiore con layout e barra di scorrimento.
     */
    public PannelloAvversari() {
        this.setLayout(new BorderLayout(15, 5));
        this.setBackground(COLORE_PANNELLO);
        this.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        pannelloGiocatori.setOpaque(false);
        turnoCorrente.setFont(StileUI.caricaFont());
        turnoCorrente.setForeground(Color.WHITE);

        pannelloGiocatori.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 5));

        pannelloScorrimento = new JScrollPane(pannelloGiocatori);
        pannelloScorrimento.setOpaque(false);
        pannelloScorrimento.getViewport().setOpaque(false);
        pannelloScorrimento.setBorder(BorderFactory.createEmptyBorder());
        pannelloScorrimento.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pannelloScorrimento.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        pannelloScorrimento.setPreferredSize(new Dimension(600, 60));

        this.add(turnoCorrente, BorderLayout.WEST);
        this.add(pannelloScorrimento, BorderLayout.CENTER);
    }

    /**
     * Inizializza le etichette per tutti i giocatori della partita.
     * 
     * @param giocatori la lista dei giocatori
     */
    public void impostaGiocatori(List<Giocatore> giocatori) {
        pannelloGiocatori.removeAll();
        etichetteGiocatori.clear();

        for (Giocatore g : giocatori) {
            JLabel testo = new JLabel(g.nome + ": " + g.getNumeroCarte() + " carte");
            testo.setFont(StileUI.caricaFont());
            testo.setForeground(Color.WHITE);
            etichetteGiocatori.put(g, testo);
            pannelloGiocatori.add(testo);
        }

        pannelloGiocatori.revalidate();
        pannelloGiocatori.repaint();
        if (pannelloScorrimento != null) {
            pannelloScorrimento.revalidate();
            pannelloScorrimento.repaint();
        }
    }

    /**
     * Aggiorna a schermo le carte rimaste, i punti e il giocatore di turno.
     * 
     * @param corrente       il giocatore di turno
     * @param tuttiGiocatori tutti i giocatori
     * @param mappaPunteggio mappa con i punteggi
     * @param modalitaPunti  true se si gioca a punti
     */
    public void aggiornaStato(Giocatore corrente, List<Giocatore> tuttiGiocatori,
            Map<Giocatore, Integer> mappaPunteggio, boolean modalitaPunti) {
        if (corrente != null) {
            turnoCorrente.setText("Turno di: " + corrente.nome);
        }

        for (Giocatore g : tuttiGiocatori) {
            JLabel testo = etichetteGiocatori.get(g);
            if (testo != null) {
                String punti = modalitaPunti ? " (" + mappaPunteggio.getOrDefault(g, 0) + " punti)" : "";
                testo.setText(g.nome + ": " + g.getNumeroCarte() + " carte" + punti);

                if (g.equals(corrente)) {
                    testo.setBackground(new Color(133, 112, 0));
                    testo.setFont(StileUI.caricaFont());
                    testo.setOpaque(true);
                } else {
                    testo.setForeground(Color.WHITE);
                    testo.setFont(StileUI.caricaFont());
                    testo.setOpaque(false);
                }
            }
        }
    }
}
