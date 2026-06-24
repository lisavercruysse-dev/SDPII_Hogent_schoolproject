package org.sdp.sdp.gui;

import domein.JobTitel;
import domein.WerknemerController;
import dto.WerknemerDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class LoginSchermController extends VBox {

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtWachtwoord;
    @FXML private Label lblFout;

    private final WerknemerController werknemerController;
    private LoginCallback onLoginSuccess;

    public LoginSchermController(WerknemerController werknemerController) {
        this.werknemerController = werknemerController;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org.sdp.sdp/gui/LoginScherm.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleLogin() {
        String email = txtEmail.getText().trim();
        String wachtwoord = txtWachtwoord.getText();

        lblFout.setText("");

        if (email.isBlank() || wachtwoord.isBlank()) {
            lblFout.setText("Vul alle velden in.");
            return;
        }

        try {
            WerknemerDTO werknemer = werknemerController.login(email, wachtwoord);
            if (onLoginSuccess != null) {
                onLoginSuccess.onSuccess(werknemer);
            }
        } catch (IllegalAccessException e) {
            lblFout.setText("Geen toegang tot de applicatie.");
            txtWachtwoord.clear();
        } catch (IllegalArgumentException e) {
            lblFout.setText("Ongeldig e-mailadres of wachtwoord.");
            txtWachtwoord.clear();
        } catch (IllegalStateException e) {
            lblFout.setText("Je account is inactief. Contacteer een administrator.");
            txtWachtwoord.clear();
        }
    }

    public void setOnLoginSuccess(LoginCallback callback) {
        this.onLoginSuccess = callback;
    }

    @FunctionalInterface
    public interface LoginCallback {
        void onSuccess(WerknemerDTO werknemer);
    }
}