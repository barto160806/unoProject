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
    private JCheckBox Stacking;
    private JCheckBox NumberRush;
    private JCheckBox SevenZero;
    private JCheckBox partitaSingola;
    private JCheckBox partitaPunti;
    private JTextField Soglia;

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
        Soglia = new JTextField("500", 5);
        Soglia.setEnabled(false);

        pannelloModalità.add(partitaSingola);
        pannelloModalità.add(partitaPunti);
        pannelloModalità.add(Box.createHorizontalStrut(15));
        pannelloModalità.add(testoSoglia);
        pannelloModalità.add(Soglia);

        pannelloCentrale.add(pannelloModalità);
        pannelloCentrale.add(Box.createVerticalStrut(10));

        // Pannello Varianti di gioco
        JPanel pannelloVarianti = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        pannelloVarianti.setBorder(BorderFactory.createTitledBorder("Varianti di Gioco Attive"));
        pannelloVarianti.setOpaque(false);

        Stacking = new JCheckBox("Stacking");
        NumberRush = new JCheckBox("NumberRush");
        SevenZero = new JCheckBox("SevenZero");

        pannelloVarianti.add(Stacking);
        pannelloVarianti.add(NumberRush);
        pannelloVarianti.add(SevenZero);

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
        bottoneAvvia.setActionCommand("AVVIA");
        bottoneAvvia.addActionListener(listener);

        pannelloInferiore.add(bottoneAnnulla);
        pannelloInferiore.add(bottoneAvvia);

        pannelloPrincipale.add(pannelloInferiore, BorderLayout.SOUTH);
        this.setContentPane(pannelloPrincipale);
    }

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

    private void salvaEConferma() {

        String testoSoglia = Soglia.getText().trim();
        sogliaVittoria = checkSoglia(testoSoglia);

        modalitaPunti = partitaPunti.isSelected();
        varianteStacking = Stacking.isSelected();
        varianteNumberRush = NumberRush.isSelected();
        varianteSevenZero = SevenZero.isSelected();

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

    // Implementazione Listener
    private class ConfigurazioneListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String comando = e.getActionCommand();
            switch (comando) {
                case "MODALITA" -> Soglia.setEnabled(partitaPunti.isSelected());
                case "AVVIA" -> salvaEConferma();
                case "ANNULLA" -> PopUpConfigurazione.this.dispose();
            }
        }
    }

    // Metodi Getter
    public boolean isConfermata() {
        return confermata;
    }

    public boolean isModalitaPunti() {
        return modalitaPunti;
    }

    public int getSogliaVittoria() {
        return sogliaVittoria;
    }

    public boolean isVarianteStacking() {
        return varianteStacking;
    }

    public boolean isVarianteNumberRush() {
        return varianteNumberRush;
    }

    public boolean isVarianteSevenZero() {
        return varianteSevenZero;
    }

    public List<GiocatoreConfig> getGiocatoriConfig() {
        return giocatoriConfig;
    }
}
