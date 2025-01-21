package org.openjfx.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.openjfx.MyApp;

import java.util.Locale;
public class LoginController {
    @FXML
    private TextField tb_usernameField;
    @FXML
    private PasswordField tb_passwordField;
    @FXML
    private Label lb_errorLabel;
    // Referenz auf MyApp, damit dort die Hauptszene aufgerufen werden kann
    private MyApp myApp;
    public void setMyApp(MyApp myApp) {
        this.myApp = myApp;
    }
    @FXML
    private void handleLogin() {
        String user = tb_usernameField.getText();
        String pass = tb_passwordField.getText();
        // Beispiel: simpler Hardcode-Test
        if (checkCredentials(user, pass)) {
            // Wenn OK -> Hauptszene anzeigen
            myApp.showMainScene(Locale.getDefault());
        } else {
            // Fehlermeldung sichtbar machen
            lb_errorLabel.setVisible(true);
            //für jetzt immer true, später ändern?
            // myApp.showMainScene(Locale.getDefault());
        }
    }
    private boolean checkCredentials(String user, String pass) {
        // In einer echten App anders
        return "admin".equals(user) && "geheim".equals(pass);
    }
}