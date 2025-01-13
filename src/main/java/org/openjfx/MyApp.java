package org.openjfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;


public class MyApp extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        showLogin();
    }

    public void showLogin() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();

            // Controller holen und MyApp übergeben
            LoginController loginCtrl = loader.getController();
            loginCtrl.setMyApp(this);

            Scene scene = new Scene(root, 400, 300); // z.B. 400x300
            primaryStage.setTitle("Login");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeLocale(Locale locale) {
        showMainScene(locale);
    }
    public void showMainScene(Locale locale) {
        try {

             if(locale == null) {
                 locale = Locale.getDefault();
             }

            ResourceBundle bundle = ResourceBundle.getBundle("locale.messages", locale);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("scene.fxml"), bundle);
            Parent root = loader.load();

            FXMLController controller = loader.getController();
            controller.setMyApp(this);  // Übergib die MyApp-Instanz

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
            primaryStage.setTitle("Discord-Bot");
            primaryStage.setScene(scene);
            primaryStage.show();

            // Mindestgröße
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(700);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}