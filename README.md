# Progetto UNO
Questo documento contiene i requisiti tecnici, la struttura del progetto e tutte le istruzioni necessarie per avviare correttamente il gioco.
---
### 1. Ambiente di Esecuzione

* **Piattaforma Java**: JDK / JavaSE versione 25.
* **Framework Grafico**: `javax.swing` (costruzione dell'interfaccia utente) e `java.awt` (gestione layout e timer dei bot).
* **Dipendenze**: Sviluppato interamente senza librerie esterne.

---

### 2. Struttura della Repository e Risorse

Affinché il progetto carichi correttamente gli elementi grafici e i font a runtime, il file system deve mantenere la seguente organizzazione:

| Percorso | Tipologia | Descrizione |
| :--- | :--- | :--- |
| `backend/` | Codice sorgente | Contiene le classi che rappresentano il modello del gioco. |
| `gui/` | Codice sorgente | Contiene l'interfaccia grafica e la gestione della partita. |
| `gui/images/` | Risorse grafiche | Contiene i file per la visualizzazione delle 108 carte di gioco. |
| `gui/Fredoka.ttf` | Risorsa tipografica | Font TrueType caricato dinamicamente a runtime per lo stile dell'interfaccia. |
| `JavaDOC/` | Documentazione Tecnica | Documentazione generata in formato HTML tramite Javadoc. |
| `relazioneUNO.pdf` | Documento PDF | Relazione descrittiva del progetto, con UML annesso e dettagli sulle varianti. |

---

### 3. Modalità di Avvio e Istruzioni di Gioco

* **Classe principale da eseguire**: `gui.FinestraPrincipale`.
* **Avvio della partita**: Eseguire il `main` nella classe principale, selezionare modalità e giocatori, quindi cliccare su **Avvia partita**.

---

### 4. Consultazione della Documentazione

* **Documentazione del Codice (JavaDoc)**: Per consultare la specifica dettagliata delle classi, aprire il file `index.html` presente nella directory `JavaDOC/`.
* **Manuale di avvio e dettagli del progetto**: La guida all'avvio dell'applicazione e i dettagli di progetto sono consultabili nel file `RelazioneUNO.pdf`.
