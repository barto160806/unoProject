package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Finestra di dialogo per configurare una nuova partita di UNO:
 * modalità di gioco (singola o a punti con soglia), varianti attive (Stacking,
 * Number Rush, Seven-Zero)
 * e numero di giocatori con relativi tipi (Umano, Bot Casuale, Bot Aggressivo).
 */
public class PopUpConfigurazione extends JDialog {

    private boolean confermata = false;
    private int contatoreGiocatori = 0;

    private boolean modalitaPunti = false;
    private int sogliaVittoria = 500;
    private boolean varianteStacking = false;
    private boolean varianteNumberRush = false;
    private boolean varianteSevenZero = false;
    private List<GiocatoreConfig> giocatoriConfig = new ArrayList<>();

    private JSlider selezioneNumeroGiocatori;
    private JPanel pannelloListaGiocatori;
    private JCheckBox stacking;
    private JCheckBox numberRush;
    private JCheckBox sevenZero;
    private JCheckBox partitaSingola;
    private JCheckBox partitaPunti;
    private JTextField soglia;

    /**
     * Dati di configurazione per un singolo giocatore (nome e tipo).
     */
    public static class GiocatoreConfig {
        /** Nome del giocatore. */
        public String nome;
        /** Tipo di partecipante ("Umano", "Bot Casuale", "Bot Aggressivo"). */
        public String tipo;

        /**
         * Crea la configurazione di un partecipante.
         * 
         * @param nome il nome impostato
         * @param tipo il tipo di giocatore
         */
        public GiocatoreConfig(String nome, String tipo) {
            this.nome = nome;
            this.tipo = tipo;
        }
    }

    /**
     * Crea la finestra di dialogo per la configurazione della partita.
     */
    public PopUpConfigurazione() {
        super((Frame) null, "Configurazione nuova partita UNO", true);
        this.setSize(600, 720);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setResizable(false);
        ConfigurazioneListener listener = new ConfigurazioneListener();

        JPanel pannelloPrincipale = new JPanel(new BorderLayout(15, 15));
        pannelloPrincipale.setBorder(new EmptyBorder(15, 15, 15, 15));
        pannelloPrincipale.setBackground(new Color(255, 255, 255));

        JLabel titolo = new JLabel("Configura la Partita", SwingConstants.CENTER);
        titolo.setFont(StileUI.caricaFont());

        pannelloPrincipale.add(titolo, BorderLayout.NORTH);

        JPanel pannelloCentrale = new JPanel();
        pannelloCentrale.setLayout(new BoxLayout(pannelloCentrale, BoxLayout.Y_AXIS));
        pannelloCentrale.setOpaque(false);

        JPanel pannelloModalità = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pannelloModalità.setBorder(BorderFactory.createTitledBorder("Modalità di gioco"));
        pannelloModalità.setOpaque(false);

        partitaSingola = new JCheckBox("Partita Singola", true);
        partitaSingola.setActionCommand("MODALITA");
        partitaSingola.addActionListener(listener);

        partitaPunti = new JCheckBox("Partita a Punti");
        partitaPunti.setActionCommand("MODALITA");
        partitaPunti.addActionListener(listener);

        ButtonGroup bg = new ButtonGroup();
        bg.add(partitaSingola);
        bg.add(partitaPunti);

        JLabel testoSoglia = new JLabel("Soglia vittoria:");
        soglia = new JTextField("500", 5);
        soglia.setEnabled(false);

        pannelloModalità.add(partitaSingola);
        pannelloModalità.add(partitaPunti);
        pannelloModalità.add(Box.createHorizontalStrut(15));
        pannelloModalità.add(testoSoglia);
        pannelloModalità.add(soglia);

        pannelloCentrale.add(pannelloModalità);
        pannelloCentrale.add(Box.createVerticalStrut(10));

        // Pannello Varianti di gioco
        JPanel pannelloVarianti = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        pannelloVarianti.setBorder(BorderFactory.createTitledBorder("Varianti di Gioco Attive"));
        pannelloVarianti.setOpaque(false);

        stacking = new JCheckBox("Stacking");
        numberRush = new JCheckBox("NumberRush");
        sevenZero = new JCheckBox("SevenZero");

        pannelloVarianti.add(stacking);
        pannelloVarianti.add(numberRush);
        pannelloVarianti.add(sevenZero);

        pannelloCentrale.add(pannelloVarianti);
        pannelloCentrale.add(Box.createVerticalStrut(10));

        // Pannello Selezione Giocatori
        JPanel pannelloSelezioneGiocatori = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        pannelloSelezioneGiocatori.setOpaque(false);
        pannelloSelezioneGiocatori.setBorder(BorderFactory.createTitledBorder("Numero giocatori"));

        selezioneNumeroGiocatori = new JSlider(2, 6, 2);
        pannelloSelezioneGiocatori.add(selezioneNumeroGiocatori);
        selezioneNumeroGiocatori.setMajorTickSpacing(1);
        selezioneNumeroGiocatori.setPaintLabels(true);
        selezioneNumeroGiocatori.addChangeListener(evento -> aggiornaListaGiocatoriGUI());
        pannelloCentrale.add(pannelloSelezioneGiocatori);

        // Pannello Lista Giocatori
        pannelloListaGiocatori = new JPanel();
        pannelloListaGiocatori.setLayout(new GridLayout(0, 1));
        pannelloListaGiocatori.setOpaque(false);

        JScrollPane scrollGiocatori = new JScrollPane(pannelloListaGiocatori);
        scrollGiocatori.setPreferredSize(new Dimension(520, 220));
        scrollGiocatori.setBorder(BorderFactory.createTitledBorder("Elenco Giocatori"));
        pannelloCentrale.add(scrollGiocatori);
        aggiornaListaGiocatoriGUI();

        pannelloPrincipale.add(pannelloCentrale, BorderLayout.CENTER);

        // Pulsanti
        JPanel pannelloInferiore = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        pannelloInferiore.setOpaque(false);

        JButton bottoneAnnulla = new JButton("Annulla");
        bottoneAnnulla.setActionCommand("ANNULLA");
        bottoneAnnulla.addActionListener(listener);

        JButton bottoneAvvia = new JButton("AVVIA PARTITA");
        bottoneAvvia.setFont(StileUI.caricaFont());
        bottoneAvvia.setBackground(new Color(56, 142, 60));
        bottoneAvvia.setForeground(Color.WHITE);
        bottoneAvvia.setOpaque(true);
        bottoneAvvia.setContentAreaFilled(true);
        bottoneAvvia.setBorderPainted(false);
        bottoneAvvia.setActionCommand("AVVIA");
        bottoneAvvia.addActionListener(listener);

        pannelloInferiore.add(bottoneAnnulla);
        pannelloInferiore.add(bottoneAvvia);

        pannelloPrincipale.add(pannelloInferiore, BorderLayout.SOUTH);
        this.setContentPane(pannelloPrincipale);
    }

    /**
     * Aggiorna la lista visuale dei giocatori nel pannello di configurazione
     * in base al numero selezionato dallo slider.
     */
    private void aggiornaListaGiocatoriGUI() {
        pannelloListaGiocatori.removeAll();
        int count = (int) selezioneNumeroGiocatori.getValue();

        String[] tipiDisponibili = { "Umano", "Bot Casuale", "Bot Aggressivo" };

        for (int i = 0; i < count; i++) {
            JPanel pannelloNomi = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
            pannelloNomi.setOpaque(false);

            JLabel nomeGiocatore = new JLabel("Giocatore " + (i + 1) + ":");
            nomeGiocatore.setPreferredSize(new Dimension(80, 25));

            String tipoDefault = (i == 0) ? "Umano" : "Bot Casuale";

            String nomeDefault = (i == 0) ? "Giocatore 1" : "Giocatore " + (i + 1);

            JTextField inserimentoNome = new JTextField(nomeDefault, 12);

            JComboBox<String> cambiaTipoGiocatore = new JComboBox<>(tipiDisponibili);
            cambiaTipoGiocatore.setSelectedItem(tipoDefault);

            pannelloNomi.add(nomeGiocatore);
            pannelloNomi.add(inserimentoNome);
            pannelloNomi.add(cambiaTipoGiocatore);

            pannelloListaGiocatori.add(pannelloNomi);
        }

        pannelloListaGiocatori.revalidate();
        pannelloListaGiocatori.repaint();
    }

    /**
     * Valida e converte la soglia di vittoria inserita dall'utente.
     * Se il valore è vuoto o contiene caratteri non numerici, ritorna 500.
     *
     * @param soglia il testo inserito dall'utente
     * @return il valore numerico della soglia, o 500 come fallback
     */
    private int checkSoglia(String soglia) {
        if (soglia.equals(""))
            return 500;
        String[] arrayCaratteri = soglia.split("");
        String numeri = "0123456789";
        boolean condizione = true;
        for (String carattere : arrayCaratteri) {
            if (!numeri.contains(carattere)) {
                condizione = false;
                break;
            } else {
                continue;
            }
        }
        if (condizione) {
            return Integer.parseInt(soglia);
        } else {
            return 500;
        }
    }

    /**
     * Raccoglie tutte le impostazioni dalla GUI (modalità, varianti,
     * lista giocatori), salva la configurazione e chiude il dialogo.
     */
    private void salvaEConferma() {

        String testoSoglia = soglia.getText().trim();
        sogliaVittoria = checkSoglia(testoSoglia);

        modalitaPunti = partitaPunti.isSelected();
        varianteStacking = stacking.isSelected();
        varianteNumberRush = numberRush.isSelected();
        varianteSevenZero = sevenZero.isSelected();

        giocatoriConfig.clear();
        Component[] componentiPannelloGiocatori = pannelloListaGiocatori.getComponents();
        for (Component comp : componentiPannelloGiocatori) {
            if (comp instanceof JPanel pannello) {
                Component[] componentiInterni = pannello.getComponents();

                if (componentiInterni.length >= 3
                        && componentiInterni[1] instanceof JTextField nomeInputGiocatore
                        && componentiInterni[2] instanceof JComboBox<?> selezioneTipoGiocatore) {

                    String nome = nomeInputGiocatore.getText().trim();
                    String tipo = String.valueOf(selezioneTipoGiocatore.getSelectedItem());

                    if (nome.equals(""))
                        nome = "Giocatore" + ++contatoreGiocatori;

                    giocatoriConfig.add(new GiocatoreConfig(nome, tipo));
                }
            }
        }

        confermata = true;
        this.dispose();
    }

    /**
     * Listener interno che gestisce gli eventi dei pulsanti della finestra
     * di configurazione (cambio modalità, avvio partita, annullamento).
     */
    private class ConfigurazioneListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String comando = e.getActionCommand();
            switch (comando) {
                case "MODALITA" -> soglia.setEnabled(partitaPunti.isSelected());
                case "AVVIA" -> salvaEConferma();
                case "ANNULLA" -> PopUpConfigurazione.this.dispose();
            }
        }
    }

    /**
     * Indica se l'utente ha confermato la configurazione premendo "AVVIA PARTITA".
     *
     * @return {@code true} se la configurazione è stata confermata
     */
    public boolean isConfermata() {
        return confermata;
    }

    /**
     * Indica se è stata selezionata la modalità a punti.
     *
     * @return {@code true} se si gioca a punti, {@code false} per partita singola
     */
    public boolean isModalitaPunti() {
        return modalitaPunti;
    }

    /**
     * Restituisce la soglia di punti necessaria per vincere la partita.
     *
     * @return la soglia di vittoria (default 500)
     */
    public int getSogliaVittoria() {
        return sogliaVittoria;
    }

    /**
     * Indica se la variante Stacking è attiva.
     *
     * @return {@code true} se lo stacking è abilitato
     */
    public boolean isVarianteStacking() {
        return varianteStacking;
    }

    /**
     * Indica se la variante NumberRush è attiva.
     *
     * @return {@code true} se NumberRush è abilitato
     */
    public boolean isVarianteNumberRush() {
        return varianteNumberRush;
    }

    /**
     * Indica se la variante SevenZero è attiva.
     *
     * @return {@code true} se SevenZero è abilitato
     */
    public boolean isVarianteSevenZero() {
        return varianteSevenZero;
    }

    /**
     * Restituisce la lista delle configurazioni dei giocatori impostate dall'utente.
     *
     * @return la lista di {@link GiocatoreConfig} con nomi e tipi dei partecipanti
     */
    public List<GiocatoreConfig> getGiocatoriConfig() {
        return giocatoriConfig;
    }
}
