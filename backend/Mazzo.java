package backend;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Arrays;

/**
 * Gestisce la creazione e il mescolamento del mazzo da 108 carte di UNO.
 */
public class Mazzo {
    /**
     * Crea un mazzo completo da 108 carte e lo mescola.
     * 
     * @return lista di carte mescolate
     */
    public static List<Carta> inizializzaMazzo() {
        List<Carta> mazzo = new ArrayList<>();

        Colore[] colori = Colore.values();
        Colore[] coloriModificati = Arrays.copyOfRange(colori, 0, 4);
        Valore[] valori = Valore.values();
        Valore[] valoriModificati = Arrays.copyOfRange(valori, 1, valori.length - 2);

        for (Colore colore : coloriModificati) {
            // Uno 0 per ogni colore
            mazzo.add(new Carta(colore, Valore.ZERO));
            // Due carte per ciascun valore (1-9, Salta, Inverti, +2)
            for (Valore valore : valoriModificati) {
                mazzo.add(new Carta(colore, valore));
            }
            for (Valore valore : valoriModificati) {
                mazzo.add(new Carta(colore, valore));
            }
        }

        // 4 Jolly e 4 +4
        for (int i = 0; i < 4; i++) {
            mazzo.add(new Carta(Colore.NESSUNO, Valore.WILD));
        }
        for (int i = 0; i < 4; i++) {
            mazzo.add(new Carta(Colore.NESSUNO, Valore.WILD_DRAW_FOUR));
        }

        Collections.shuffle(mazzo);
        return mazzo;
    }

    /**
     * Mescola una lista di carte.
     * 
     * @param mazzoDaMescolare la lista di carte da mescolare
     */
    public static void mescola(List<Carta> mazzoDaMescolare) {
        Collections.shuffle(mazzoDaMescolare);
    }
}
