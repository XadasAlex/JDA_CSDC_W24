package org.openjfx.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import org.openjfx.MyApp;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Locale;

public class LanguageController implements Initializable {

    @FXML
    private ComboBox<String> localeComboBox;

    // Referenz auf MyApp, um später die Sprache zu ändern
    private MyApp myApp;

    // Eine Map von "Anzeigename" -> "Locale"
    private final Map<String, Locale> availableLocales = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Hier trägst du die Sprachen ein, die du unterstützen möchtest:
        availableLocales.put("Deutsch", new Locale("de", "DE"));
        availableLocales.put("English", new Locale("en", "US"));
        availableLocales.put("Español", new Locale("es", "ES"));
        availableLocales.put("French", new Locale("fr", "FR"));
        availableLocales.put("Italian", new Locale("it", "IT"));

        // Keys (Deutsch, English, Español) in die ComboBox einfügen
        localeComboBox.getItems().addAll(availableLocales.keySet());

        if (myApp != null) {
            setComboBoxToCurrentLocale();
        }
    }

    public void setMyApp(MyApp myApp) {
        this.myApp = myApp;

        // Wenn myApp gesetzt wird, ComboBox aktualisieren
        setComboBoxToCurrentLocale();
    }
        // Wenn myApp gesetzt wird, ComboBox aktualisieren

    private void setComboBoxToCurrentLocale() {
        if (myApp == null) return;

        Locale currentLocale = myApp.getCurrentLocale(); // Aktuelle Locale abrufen
        String selectedLanguage = availableLocales.entrySet().stream()
                .filter(entry -> entry.getValue().equals(currentLocale)) // Locale finden
                .map(Map.Entry::getKey) // Anzeigename (z. B. Deutsch)
                .findFirst()
                .orElse("Deutsch"); // Fallback, falls nicht gefunden

        localeComboBox.setValue(selectedLanguage);
    }
    @FXML
    private void changeLocale() {
        // String, der im ComboBox angezeigt wird
        String chosenLang = localeComboBox.getValue();

        // Entsprechende Locale aus unserer Map auslesen
        Locale selectedLocale = availableLocales.getOrDefault(chosenLang, Locale.getDefault());

        // Jetzt im MyApp die Methode aufrufen, welche die Hauptszene neu lädt
        myApp.changeLocale(selectedLocale);
    }
}

