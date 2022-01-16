package controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.DatabaseConnector;
import model.Korpus;
import model.MarkovChain;
import model.Text;

import java.io.IOException;
import java.util.Arrays;


public class MarkovChainTextGenerationGUIController {


    private DatabaseConnector dbCon = new DatabaseConnector();
    private Korpus currentKorpus = new Korpus();
    private MarkovChain currentChain = new MarkovChain();
    private Text currentText;
    private ObservableList<String> availableChains = FXCollections.observableArrayList();
    private ObservableList<String> availableKorpusses = FXCollections.observableArrayList();
    private ObservableList<String> availableTexts = FXCollections.observableArrayList();
    @FXML
    private ComboBox<String> mainWindow_ChainSelect;
    @FXML
    private ComboBox<String> generateChain_KorpusChoice;
    @FXML
    private TextArea mainWindow_TextOutput;
    @FXML
    private ComboBox<String> deleteKorpus_KorpusSelect;
    @FXML
    private TextArea newKorp_Text;
    @FXML
    private TextField newKorp_Title;
    @FXML
    private ChoiceBox<String> generateChain_OrderChoice;
    @FXML
    private ComboBox<String> deleteText_TextChoice;
    @FXML
    private ComboBox<String> deleteChain_ChainChoice;


    // Open Popups

    /**
     * Opens the New Korpus window
     */
    public void openNewKorpus() {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("/view/NewKorpus.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Neuer Korpus");
            stage.setScene(new Scene(root, 600, 400));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * opens the Delete Korpus window
     */
    public void openDeleteKorpus() {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("/view/DeleteKorpus.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Korpus löschen");
            stage.setScene(new Scene(root, 400, 200));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * opens the chain generation window
     */
    public void openGenerateChain() {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("/view/GenerateChain.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Chain generieren");
            stage.setScene(new Scene(root, 400, 200));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * opens the chain deletion window
     */
    public void openDeleteChain() {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("/view/DeleteChain.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Chain löschen");
            stage.setScene(new Scene(root, 400, 200));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * saves the current text in the database if the title is unique, opens a popup to inform the user about success or failure
     */
    public void openSaveText() {
        if (currentText != null) {
            if (dbCon.checkForDuplicate(currentText.getTitle(), "text")) {
                if (dbCon.saveText(currentText)) {
                    openGeneralPopup("Erfolgreich gespeichert.");
                }
            } else {
                openGeneralPopup("Es gibt bereits einen Eintrag mit diesem Titel");
            }
        } else {
            openGeneralPopup("Fehler beim speichern.");
        }
    }

    /**
     * opens the load text window, uses the LoadTextController
     */
    public void openLoadText() {
        FXMLLoader loader;
        Parent root;
        try {
            loader = new FXMLLoader(getClass().getResource("/view/LoadText.fxml"));
            root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Text laden");
            stage.setScene(new Scene(root, 400, 200));
            stage.show();
            LoadTextController controller = loader.getController();
            controller.initialize(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * opens the Text deletion window
     */
    public void openDeleteText() {
        FXMLLoader loader;
        Parent root;
        try {
            loader = new FXMLLoader(getClass().getResource("/view/DeleteText.fxml"));
            root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Text löschen");
            stage.setScene(new Scene(root, 400, 200));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * opens a general popup that displays a given message
     *
     * @param message the message to display
     */
    public void openGeneralPopup(String message) {
        FXMLLoader loader;
        Parent root;
        try {
            loader = new FXMLLoader(getClass().getResource("/view/GeneralPopup.fxml"));
            root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Achtung");
            stage.setScene(new Scene(root, 450, 200));
            stage.show();
            GeneralPopupController controller = loader.getController();
            controller.changeMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * opens a window that allows the user to display a korpus, uses the ShowKorpusController class
     */
    public void openShowKorpusWindow() {
        FXMLLoader loader;
        Parent root;
        try {
            loader = new FXMLLoader(getClass().getResource("/view/ShowKorpus.fxml"));
            root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Korpus anzeigen");
            stage.setScene(new Scene(root, 400, 200));
            stage.show();
            ShowKorpusController controller = loader.getController();
            controller.initialize(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * opens a window that allows the user to specify the length of the desired text, uses CustomTextController class
     */
    public void openCustomText() {
        FXMLLoader loader;
        Parent root;
        try {
            loader = new FXMLLoader(getClass().getResource("/view/CustomText.fxml"));
            root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Manuelle Texteinstellungen");
            stage.setScene(new Scene(root, 400, 300));
            stage.show();
            CustomTextController controller = loader.getController();
            controller.initialize(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Functions

    /**
     * lists the available chains in the main window combobox
     */
    public void listAvailableChains() {
        availableChains.clear();
        String[] chains = dbCon.listAvailableChains().split("\\r?\\n");
        availableChains.addAll(Arrays.asList(chains));
        mainWindow_ChainSelect.setItems(availableChains);
    }

    /**
     * lists the available chains in the delete window combo box
     */
    public void listAvailableChainsDeleteWindow() {
        availableChains.clear();
        String[] chains = dbCon.listAvailableChains().split("\\r?\\n");
        availableChains.addAll(Arrays.asList(chains));
        deleteChain_ChainChoice.setItems(availableChains);
    }

    /**
     * sets the current chain as the selection from the main window combo box
     */
    public void setCurrentChain() {
        if (mainWindow_ChainSelect.getValue() != null) {
            currentChain = dbCon.loadChain(mainWindow_ChainSelect.getValue());
        }
    }

    /**
     *  lists the available Korpusses in the generate Chain window combo box
     */
    public void listAvailableKorpusses() {
        availableKorpusses.clear();
        String[] korpusses = dbCon.listAvailableKorpusses().split("\\r?\\n");
        availableKorpusses.addAll(Arrays.asList(korpusses));
        generateChain_KorpusChoice.setItems(availableKorpusses);
    }

    /**
     * lists the available Korpusses in the delete window combo box
     */
    public void listAvailableKorpussesDeleteWindow() {
        availableKorpusses.clear();
        String[] korpusses = dbCon.listAvailableKorpusses().split("\\r?\\n");
        availableKorpusses.addAll(Arrays.asList(korpusses));
        deleteKorpus_KorpusSelect.setItems(availableKorpusses);

    }

    /**
     * sets the current Korpus as the choice from the combo box in the generate chain window
     */
    public void setCurrentKorpus() {
        if (generateChain_KorpusChoice.getValue() != null) {
            currentKorpus = dbCon.loadKorpus(generateChain_KorpusChoice.getValue());
        }
    }

    /**
     * sets the current Korpus as the choice from the combo box in the delete korpus window
     */
    public void setCurrentKorpusDeleteWindow() {
        if (deleteKorpus_KorpusSelect.getValue() != null) {
            currentKorpus = dbCon.loadKorpus(deleteKorpus_KorpusSelect.getValue());
        }
    }

    /**
     * generates a sentence and displays it in the main window text area
     */
    public void generateSentence() {
        if (currentChain.getName() != null) {
            mainWindow_TextOutput.setText(currentChain.generateSentence());
        } else {

            openGeneralPopup("Bitte wählen sie eine Chain aus.");
        }
    }

    /**
     * generates a title and three paragraphs with 3-6 sentences per paragraph
     * and displays them in the main window text area
     */
    public void generateText() {
        if (currentChain.getName() != null) {

            currentText = new Text(currentChain.generateTitle(), currentChain.generateText());
            mainWindow_TextOutput.setText(currentText.getTitle() + "\n\n\n" + currentText.getText());
        } else {

            openGeneralPopup("Bitte wählen Sie eine Chain aus.");
        }
    }

    /**
     * Takes a number of paragraphs and the sentences per paragraph, generates a text with those parameters
     * and displays it in the main window text area
     * @param paragraphs number of desired paragraphs to generate
     * @param sentencesPerParagraph number of sentences per paragraph
     */
    public void showCustomText(int paragraphs, int sentencesPerParagraph) {
        currentText = new Text(currentChain.generateTitle(), currentChain.generateText(paragraphs, sentencesPerParagraph));
        mainWindow_TextOutput.setText(currentText.getTitle() + "\n\n\n" + currentText.getText());
    }

    /**
     * takes a new korpus from the create korpus window text area, closes the window when successfull and displays
     * information to the user
     * @param event button press to identify the window
     */
    public void createNewKorpus(ActionEvent event) {
        if (newKorp_Title.getText().equals("")) {
            openGeneralPopup("Bitte geben Sie einen Titel ein.");
        } else if (newKorp_Text.getText().equals("")) {
            openGeneralPopup("Bitte geben Sie den Inhalt ein.");
        } else {
            if (dbCon.checkForDuplicate(newKorp_Title.getText(), "korpus")) {
                currentKorpus = new Korpus(newKorp_Title.getText(), newKorp_Text.getText());
                if (dbCon.saveKorpus(currentKorpus)) {
                    openGeneralPopup("Der Korpus wurde erfolgreich erstellt");
                    closeWindow(event);
                } else {
                    openGeneralPopup("Fehler beim speichern des Korpus");
                }
            } else {
                openGeneralPopup("Ein Korpus mit diesem Namen existiert bereits");
            }
        }
    }

    /**
     * takes a Korpus and an order from the generate chain window, generates a chain and saves it
     * closes the window on success and displays information to the user
     * @param event button press to identify the window
     */
    public void generateNewChain(ActionEvent event) {
        if (currentKorpus.getTitle().equals("")) {
            openGeneralPopup("Bitte wählen Sie einen Korpus aus.");
        } else if (generateChain_OrderChoice.getValue() == null) {
            openGeneralPopup("Bitte wählen Sie eine Ordnung aus.");
        } else {
            int order = Integer.parseInt(generateChain_OrderChoice.getValue());
            currentChain = new MarkovChain(currentKorpus, order);
            if (dbCon.checkForDuplicate(currentChain.getName(), "chain")) {

                if (dbCon.saveChain(currentChain)) {
                    openGeneralPopup("Erfolgreich generiert und gespeichert.");
                    closeWindow(event);
                } else {
                    openGeneralPopup("Error: Chain konnte nicht gespeichert werden.");
                }
            } else {
                openGeneralPopup("Diese Chain existiert bereits.");
            }

        }
    }

    /**
     * displays the current text in the main window text area
     */
    public void showLoadedText() {
        mainWindow_TextOutput.setText(currentText.getTitle() + "\n\n\n" + currentText.getText());
    }

    /**
     * displays the current korpus in the main window text area
     */
    public void showLoadedKorpus() {
        mainWindow_TextOutput.setText(currentKorpus.getText());
    }

    /**
     * lists the saved texts in the delete text window combo box
     */
    public void listAvailableTexts() {
        availableTexts.clear();
        String[] texts = dbCon.listAvailableTexts().split("\\r?\\n");
        availableTexts.addAll(Arrays.asList(texts));
        deleteText_TextChoice.setItems(availableTexts);
    }

    /**
     * deletes a text chosen in the delete text window combo box from the database, displays information to the user
     * and closes the window on success
     * @param event button press to identify the window to close it
     */
    public void deleteText(ActionEvent event) {
        if (deleteText_TextChoice.getValue() != null) {
            if (dbCon.deleteText(deleteText_TextChoice.getValue())) {
                openGeneralPopup("Erfolgreich gelöscht");
                closeWindow(event);
            } else {
                openGeneralPopup("Fehler beim Löschen");
            }
        } else {
            openGeneralPopup("Bitte wählen Sie einen gespeicherten Text aus.");
        }
    }

    /**
     * delete a chain chosen in the delete chain window combo box from the database, displays information to the user
     * and closes the window on success
     * @param event button press to identify the window to close it
     */
    public void deleteChain(ActionEvent event) {
        if (deleteChain_ChainChoice.getValue() != null) {
            if (dbCon.deleteChain(deleteChain_ChainChoice.getValue())) {
                openGeneralPopup("Erfolgreich gelöscht");
                closeWindow(event);
            } else {
                openGeneralPopup("Fehler beim Löschen");
            }
        } else {
            openGeneralPopup("Bitte wählen Sie eine Chain aus.");
        }
    }

    /**
     * deletes a korpus chosen in the delete korpus window combo box from the database, displays information to the user
     * and closes the window on success
     * @param event button press to identify the window to close it
     */
    public void deleteKorpus(ActionEvent event) {
        if (deleteKorpus_KorpusSelect.getValue() != null) {
            String title = deleteKorpus_KorpusSelect.getValue();
            if (!dbCon.checkIfChainExistsThatDependsOnKorpus(title)) {
                if (dbCon.deleteKorpus(title)) {
                    openGeneralPopup("Erfolgreich gelöscht");
                    closeWindow(event);
                } else {
                    openGeneralPopup("Ein Fehler ist aufgetreten");
                }
            } else {
                openGeneralPopup("Dieser Korpus kann nicht gelöscht werden, \nes existieren noch Chains die von diesem \nKorpus abhängen");
            }
        } else {
            openGeneralPopup("Bitte wählen Sie einen Korpus aus.");
        }
    }


    // Close Windows

    /**
     * ends the program
     */
    public void close() {
        Platform.exit();
    }

    /**
     * closes the window on which an event was triggered
     * @param event the event triggered to identify the window
     */
    public void closeWindow(ActionEvent event) {
        ((Node) (event.getSource())).getScene().getWindow().hide();
    }


    // Plain getter & setter
    public DatabaseConnector getDbCon() {
        return dbCon;
    }

    public void setCurrentText(Text currentText) {
        this.currentText = currentText;
    }

    public Text getCurrentText() {
        return currentText;
    }

    public Korpus getCurrentKorpus() {
        return currentKorpus;
    }

    public void setCurrentKorpus(Korpus currentKorpus) {
        this.currentKorpus = currentKorpus;
    }

    public MarkovChain getCurrentChain() {
        return currentChain;
    }

    public void setCurrentChain(MarkovChain currentChain) {
        this.currentChain = currentChain;
    }
}

