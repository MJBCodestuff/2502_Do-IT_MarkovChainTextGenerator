package controller;

import model.Korpus;
import model.DatabaseConnector;
import model.MarkovChain;
import model.Text;

public class MarkovChainTextGenerationController {

    public DatabaseConnector dbCon;
    public Korpus currentKorpus;
    public MarkovChain currentChain;
    public Text currentText;

    /**
     * Constructor, returns Controller Object in an empty state
     */
    public MarkovChainTextGenerationController(){
        this.dbCon = new DatabaseConnector();
        this.currentKorpus = new Korpus();
        this.currentChain = new MarkovChain();
    }

    /**
     *
     * @param title Title of the new Korpus
     * @param text Content of the new Korpus
     * @return a String containing the success or failure message to display
     */
    public String createNewKorpus(String title, String text) {
        if (dbCon.checkForDuplicate(title, "korpus")) {
            currentKorpus = new Korpus(title, text);
            if(dbCon.saveKorpus(currentKorpus)){
                return "Der Korpus wurde erfolgreich erstellt";
            }else{
                return "Fehler beim speichern des Korpus";
            }
        } else {
            return "Ein Korpus mit diesem Namen existiert bereits";
        }
    }

    /**
     *
     * @param title Title of the Korpus to load into the current state
     * @return a String containing the success or failure message to display
     */
    public String loadKorpusFromDatabase(String title) {
        currentKorpus = dbCon.loadKorpus(title);
        if(currentKorpus == null){
            return"Fehler: Korpus nicht gefunden";
        }else{
            return"Erfolgreich geladen.";
        }
    }

    /**
     *
     * @param title Title of the Korpus to delete from the database
     * @return a String containing the success or failure message to display
     */
    public String deleteKorpusFromDatabase(String title) {
        if(!dbCon.checkIfChainExistsThatDependsOnKorpus(title)){
            if (dbCon.deleteKorpus(title)) {
                return "Erfolgreich gelöscht";
            } else {
                return "Ein Fehler ist aufgetreten";
            }
        }else{
            return "Dieser Korpus kann nicht gelöscht werden, es existieren noch Chains die von diesem Korpus abhängen";
        }
    }

    /**
     *
     * @param order The order of the chain, needs to be a number between 1 and 5
     * @return a String containing the success or failure message to display
     */
    public String generateNewChain(int order) {
        if(!currentKorpus.getTitle().equals("")) {

            currentChain = new MarkovChain(currentKorpus, order);
            if (dbCon.checkForDuplicate(currentChain.getName(), "chain")){

                if (dbCon.saveChain(currentChain)) {
                    return "Erfolgreich generiert und gespeichert.";
                } else {
                    return "Error";
                }
            } else {
                return "Diese Chain existiert bereits.";
            }
        }else{
            return "Bitte wählen Sie einen Korpus aus";
        }

    }

    /**
     *
     * @param title title of the Chain to delete from the Database
     * @return a String containing the success or failure message to display
     */
    public String deleteChainFromDatabase(String title) {

        if (dbCon.deleteChain(title)) {
            return "Erfolgreich gelöscht";
        } else {
            return "Ein Fehler ist aufgetreten";
        }
    }

    /**
     *
     * @param title title of the chain to load into the current state
     * @return a String containing the success or failure message to display
     */
    public String loadChainFromDatabase(String title) {
        currentChain = dbCon.loadChain(title);
        if(currentChain != null){
            return "Erfolgreich geladen";
        }else {
            return "Fehler beim laden der Chain, Chain nicht gefunden";
        }
    }

    /**
     * Generates a Text of a Title, three Paragraphs and 3-6 Sentences per paragraph
     * @return A String containing the Title and Content of the generated Text or a failure message
     */
    public String generateText(){
        if (currentChain.getName() != null){
            currentText = new Text(currentChain.generateTitle(), currentChain.generateText());
            return currentText.getTitle() + "\n\n\n" + currentText.getText();
        }else {
            return "Fehler: Keine Chain geladen";
        }
    }

    /**
     * saves the current state text in the database
     * @return a String containing the success or failure message to display
     */
    public String saveText(){
        if(currentText != null){
            if(dbCon.checkForDuplicate(currentText.getTitle(), "text")){
                if(dbCon.saveText(currentText)){
                    return "Erfolgreich gespeichert.";
                }
            }else{
                return "Es gibt bereits einen Eintrag mit diesem Titel";
            }
        }
        return "Fehler beim speichern.";
    }

    /**
     *
     * @param title title of the Text to load
     * @return A String containing the Title and Content of the generated Text or a failure message
     */
    public String loadText(String title){
        currentText = dbCon.loadText(title);
        if(currentText != null){
            return currentText.getTitle() + "\n\n\n" + currentText.getText();
        }else{
            return "Fehler beim laden";
        }
    }

    /**
     *
     * @return returns a String containing every available Text in the format Title/nTitle/n
     */
    public String showTexts(){
        return dbCon.listAvailableTexts();
    }

    /**
     *
     * @param title title of the Text to delete from the database
     * @return a String containing the success or failure message to display
     */
    public String deleteText(String title){
        if (dbCon.deleteText(title)){
            return "Erfolgreich gelöscht";
        }else{
            return "Fehler beim löschen";
        }
    }

}
