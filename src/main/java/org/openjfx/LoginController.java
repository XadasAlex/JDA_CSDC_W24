package org.openjfx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.Locale;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;


    // Referenz auf MyApp, damit dort die Hauptszene aufgerufen werden kann
    private MyApp myApp;

    public void setMyApp(MyApp myApp) {
        this.myApp = myApp;
    }

    @FXML
    private void handleLogin() {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        // Beispiel: simpler Hardcode-Test
        if (checkCredentials(user, pass)) {
            // Wenn OK -> Hauptszene anzeigen
            myApp.showMainScene(Locale.getDefault());
        } else {
            // Fehlermeldung sichtbar machen
            errorLabel.setVisible(true);

            //für jetzt immer true, später ändern?
            myApp.showMainScene(Locale.getDefault());
        }
    }

    private boolean checkCredentials(String user, String pass) {
        // In einer echten App anders
        return "admin".equals(user) && "geheim".equals(pass);
    }
}