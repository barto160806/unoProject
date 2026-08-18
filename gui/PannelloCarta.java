package gui;

import backend.*;

import javax.swing.*;
import java.awt.*;

/**
 * Componente grafico che rappresenta una singola carta di UNO
 * caricando l'immagine corrispondente.
 */
public class PannelloCarta extends JButton {

    private final Carta CARTA;
    private JButton BOTTONE;

    /**
     * Crea il componente grafico per la carta specificata.
     * 
     * @param carta la carta da mostrare, oppure null per il dorso coperto
     */
    public PannelloCarta(Carta carta) {
        this.CARTA = carta;
        inizializza();
    }

    private void inizializza() {
        BOTTONE = PannelloCarta.this;
        BOTTONE.setCursor(new Cursor(Cursor.HAND_CURSOR));
        BOTTONE.setFont(StileUI.caricaFont());
        BOTTONE.setForeground(Color.WHITE);
        BOTTONE.setFocusPainted(false);
        setIcona();
        BOTTONE.setContentAreaFilled(false);
        BOTTONE.setBorderPainted(false);
    }

    private void setIcona() {
        ImageIcon icona = calcolaIcona();
        BOTTONE.setIcon(icona);
    }

    // Carica l'immagine JPG della carta e la ridimensiona a 90x120 pixel
    private ImageIcon calcolaIcona() {
        if (CARTA == null) {
            ImageIcon iconaNulla = new ImageIcon("gui/images/cartaCoperta.jpg");
            Image iconaNullaModificata = iconaNulla.getImage().getScaledInstance(90, 120, Image.SCALE_SMOOTH);
            return new ImageIcon(iconaNullaModificata);
        }

        Valore valore = CARTA.getValoreCarta();
        if (valore == Valore.WILD || valore == Valore.WILD_DRAW_FOUR) {
            ImageIcon iconaWild = new ImageIcon("gui/images/" + valore + ".jpg");
            Image iconaModificata = iconaWild.getImage().getScaledInstance(90, 120, Image.SCALE_SMOOTH);
            return new ImageIcon(iconaModificata);
        }

        String coloreCarta = (CARTA.getColoreCarta() + "").toLowerCase();

        ImageIcon iconaCalcolata = switch (valore) {
            case ZERO -> new ImageIcon("gui/images/" + coloreCarta + "0.jpg");
            case UNO -> new ImageIcon("gui/images/" + coloreCarta + "1.jpg");
            case DUE -> new ImageIcon("gui/images/" + coloreCarta + "2.jpg");
            case TRE -> new ImageIcon("gui/images/" + coloreCarta + "3.jpg");
            case QUATTRO -> new ImageIcon("gui/images/" + coloreCarta + "4.jpg");
            case CINQUE -> new ImageIcon("gui/images/" + coloreCarta + "5.jpg");
            case SEI -> new ImageIcon("gui/images/" + coloreCarta + "6.jpg");
            case SETTE -> new ImageIcon("gui/images/" + coloreCarta + "7.jpg");
            case OTTO -> new ImageIcon("gui/images/" + coloreCarta + "8.jpg");
            case NOVE -> new ImageIcon("gui/images/" + coloreCarta + "9.jpg");
            default -> new ImageIcon("gui/images/" + coloreCarta + valore + ".jpg");
        };

        Image iconaModificata = iconaCalcolata.getImage().getScaledInstance(90, 120, Image.SCALE_SMOOTH);
        return new ImageIcon(iconaModificata);
    }

    /**
     * Restituisce la carta associata al componente.
     * 
     * @return la carta associata
     */
    public Carta getCarta() {
        return CARTA;
    }
}
