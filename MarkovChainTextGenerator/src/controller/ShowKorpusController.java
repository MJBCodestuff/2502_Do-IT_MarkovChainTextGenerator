package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import model.DatabaseConnector;
import model.Korpus;

import java.util.Arrays;

public class ShowKorpusController {

    @FXML
    private ComboBox<String> showWindow_KorpusSelect;
    private ObservableList<String> availableKorpusses = FXCollections.observableArrayList();
    private DatabaseConnector dbCon;
    private MarkovChainTextGenerationGUIController mainController;

    /**
     * sets the show korpus window to its initial state
     *
     * @param mainController the controller of the main window
     */
    public void initialize(MarkovChainTextGenerationGUIController mainController) {
        this.mainController = mainController;
        this.dbCon = mainController.getDbCon();
        mainController.setCurrentKorpus(new Korpus());
    }

    /**
     * sets the current korpus of the main window to the users choice in the show korpus window combo box
     */
    public void setCurrentKorpusShowWindow() {
        if (showWindow_KorpusSelect.getValue() != null) {
            mainController.setCurrentKorpus(dbCon.loadKorpus(showWindow_KorpusSelect.getValue()));
        }
    }

    /**
     * lists all available korpusses saved in the database in the show korpus window combo box
     */
    public void listAvailableKorpussesShowWindow() {
        mainController.setCurrentKorpus(new Korpus());
        availableKorpusses.clear();
        String[] korpusses = dbCon.listAvailableKorpusses().split("\\r?\\n");
        availableKorpusses.addAll(Arrays.asList(korpusses));
        showWindow_KorpusSelect.setItems(availableKorpusses);

    }

    /**
     * calls the show loaded korpus method in the main window controller if the user has chosen a korpus,
     * closes the window afterwards
     *
     * @param event button press event to track which window to close
     */
    public void showKorpus(ActionEvent event) {
        if (!mainController.getCurrentKorpus().getTitle().equals("")) {

            mainController.showLoadedKorpus();
            closeWindow(event);

        } else {

            mainController.openGeneralPopup("Bitte wählen sie einen Korpus aus.");
        }
    }

    /**
     * closes the show korpus window
     *
     * @param event button press event to track which window to close
     */
    public void closeWindow(ActionEvent event) {
        ((Node) (event.getSource())).getScene().getWindow().hide();
    }

}
