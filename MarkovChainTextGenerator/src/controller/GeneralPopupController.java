package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class GeneralPopupController {

    @FXML
    private Label generalPopup_Text;

    /**
     * Sets the label on the general popup to the given message
     * @param message the message to display
     */
    public void changeMessage(String message){
        generalPopup_Text.setText(message);
    }

    /**
     * closes the window when the user presses okay
     * @param event event to track the window to close
     */
    public void closeWindow(ActionEvent event){
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

}
