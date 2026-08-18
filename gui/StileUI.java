package gui;

import java.awt.Font;
import java.io.File;

/**
 * Classe di utilita' per il caricamento del font Fredoka.ttf dell'interfaccia.
 */
public class StileUI {

    /** Font principale Fredoka per i componenti dell'interfaccia. */
    public static final Font fredokaFont = caricaFont();

    /**
     * Carica il font Fredoka da disco o Arial per sicurezza.
     * 
     * @return il font caricato
     */
    public static Font caricaFont() {
        try {
            return Font.createFont(Font.TRUETYPE_FONT, new File("gui/Fredoka.ttf"))
                    .deriveFont(20f);
        } catch (Exception e) {
            return new Font("Arial Rounded MT Bold", Font.BOLD, 20);
        }
    }
}
