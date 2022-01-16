package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import model.DatabaseConnector;

import java.util.Arrays;
import java.util.function.UnaryOperator;

public class CustomTextController {

    @FXML
    private TextField customText_numberOfParagraphs;
    @FXML
    private TextField customText_sentencesPerParagraph;
    @FXML
    private ComboBox<String> customText_ChainChoice;
    private ObservableList<String> availableChains = FXCollections.observableArrayList();
    private DatabaseConnector dbCon;
    private MarkovChainTextGenerationGUIController mainController;

    /**
     * gets called after the window gets created, sets initial state
     * @param mainController controller of the main window
     */
    public void initialize(MarkovChainTextGenerationGUIController mainController){
        this.mainController = mainController;
        this.dbCon = mainController.getDbCon();
        mainController.setCurrentChain(null);
        // one formatter per input field, even if they are using the same filter
        TextFormatter<String> textFormatterSentences = new TextFormatter<>(filter);
        TextFormatter<String> textFormatterParagraphs = new TextFormatter<>(filter);
        customText_sentencesPerParagraph.setTextFormatter(textFormatterSentences);
        customText_numberOfParagraphs.setTextFormatter(textFormatterParagraphs);
    }

    /**
     * sets the current chain in the main controller to the choice from the custom text window combo box
     */
    public void setCurrentChainCustomWindow(){
        if(customText_ChainChoice.getValue() != null){
            mainController.setCurrentChain(dbCon.loadChain(customText_ChainChoice.getValue()));
        }
    }

    /**
     * sets the current chain to null and lists all available chains in the custom text window combo box
     */
    public void listAvailableChainsCustomWindow(){
        mainController.setCurrentChain(null);
        availableChains.clear();
        String [] chains = dbCon.listAvailableChains().split("\\r?\\n");
        availableChains.addAll(Arrays.asList(chains));
        customText_ChainChoice.setItems(availableChains);

    }

    /**
     * gets the settings for the custom text from the custom text window inputs and calls the show custom text method
     * in the main controller
     *
     * @param event button press to identify the window to close
     */
    public void showCustomText(ActionEvent event){
        if(mainController.getCurrentChain() != null
                && !customText_numberOfParagraphs.getText().equals("")
                && !customText_sentencesPerParagraph.getText().equals("")){
            int paragraphs = Integer.parseInt(customText_numberOfParagraphs.getText());
            int sentencesPerParagraph = Integer.parseInt(customText_sentencesPerParagraph.getText());
            if (paragraphs > 0 && sentencesPerParagraph > 0){
                mainController.showCustomText(paragraphs, sentencesPerParagraph);
                closeWindow(event);
            }else {
                mainController.openGeneralPopup("Die Werte müssen größer als Null sein.");
            }



        }else{

            mainController.openGeneralPopup("Bitte füllen Sie alle Felder aus.");
        }
    }

    /**
     * Filter for text formatter, only allows numbers
     */
    UnaryOperator<TextFormatter.Change> filter = change -> {
        String text = change.getText();

        if (text.matches("[0-9]*")) {
            return change;
        }

        return null;
    };

    /**
     * closes the window in which the event occured
     * @param event event to track which window to close
     */
    public void closeWindow(ActionEvent event){
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }


}
