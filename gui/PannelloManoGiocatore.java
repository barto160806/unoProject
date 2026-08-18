package gui;

import backend.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Pannello inferiore che mostra le carte in mano al giocatore umano
 * e permette di cliccarle per giocarle.
 */
public class PannelloManoGiocatore extends JPanel {

    private static final Color COLORE_PANNELLO = new Color(18, 69, 23);
    private final JPanel contenitoreCarte;
    private ActionListener ascoltatoreCarte;

    /**
     * Crea il pannello della mano con barra di scorrimento orizzontale.
     */
    public PannelloManoGiocatore() {
        this.setLayout(new BorderLayout());
        this.setBackground(COLORE_PANNELLO);

        JLabel titolo = new JLabel("LA TUA MANO", SwingConstants.CENTER);
        titolo.setFont(StileUI.fredokaFont.deriveFont(26f));
        titolo.setForeground(Color.WHITE);
        this.add(titolo, BorderLayout.NORTH);

        contenitoreCarte = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        contenitoreCarte.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(contenitoreCarte);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        this.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Registra l'ascoltatore per intercettare i click sulle carte.
     * 
     * @param listener il gestore degli eventi
     */
    public void registraActionListener(ActionListener listener) {
        this.ascoltatoreCarte = listener;
    }

    /**
     * Aggiorna a schermo le carte presenti nella mano del giocatore.
     * 
     * @param carteMano la lista delle carte da mostrare
     */
    public void aggiornaMano(List<Carta> carteMano) {
        contenitoreCarte.removeAll();

        if (carteMano != null) {
            for (Carta c : carteMano) {
                PannelloCarta widget = new PannelloCarta(c);
                if (ascoltatoreCarte != null) {
                    widget.addActionListener(ascoltatoreCarte);
                }
                contenitoreCarte.add(widget);
            }
        }

        contenitoreCarte.revalidate();
        contenitoreCarte.repaint();
    }
}
