package org.sdp.sdp.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class VerwijderController extends StackPane {
    private final MainController mainController;
    private final Runnable onConfirm;
    private final String title, message;

    @FXML
    private Label titleLbl, messageLbl;

    public VerwijderController(MainController mainController, String title, String message,Runnable onConfirm) {
        this.mainController = mainController;
        this.onConfirm = onConfirm;
        this.title = title;
        this.message = message;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org.sdp.sdp/gui/VerwijderConfirm.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
        titleLbl.setText(title);
        messageLbl.setText(message);
    }

    public void btnCloseAction(ActionEvent actionEvent){
        mainController.closePopup();
    }

    public void btnConfrimAction(ActionEvent actionEvent){
        try {
            onConfirm.run();
            mainController.closePopup();
        } catch (IllegalArgumentException e) {
            mainController.closePopup();
            mainController.showPopup(new VerwijderErrorController(mainController, e.getMessage()));
        }
    }
}
