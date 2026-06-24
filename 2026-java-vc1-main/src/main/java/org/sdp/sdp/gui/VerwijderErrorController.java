package org.sdp.sdp.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import lombok.Setter;

import javax.imageio.IIOException;

public class VerwijderErrorController extends StackPane {
    private final MainController mainController;
    private final String message;

    @FXML
    private Label messageLbl;

    public VerwijderErrorController(MainController mainController, String message) {
        this.mainController = mainController;
        this.message = message;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org.sdp.sdp/gui/VerwijderError.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        messageLbl.setText(message);
    }

    public void btnCloseAction(ActionEvent actionEvent){
        mainController.closePopup();
    }

    public void onOk(ActionEvent actionEvent) {
        mainController.closePopup();
    }
}
