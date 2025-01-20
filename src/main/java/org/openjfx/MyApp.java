package org.openjfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.openjfx.controllers.GeneralController;
import org.openjfx.controllers.LoginController;
import org.openjfx.controllers.MasterController;

import java.util.Locale;
import java.util.ResourceBundle;

public class MyApp extends Application {
    private final int WIDTH = 800;
    private final int HEIGHT = 800;

    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        showLogin();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void showLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();
            // Controller holen und MyApp übergeben
            LoginController loginCtrl = loader.getController();
            loginCtrl.setMyApp(this);
            Scene scene = new Scene(root, 400, 300); // z.B. 400x300
            //Stylesheet login.css imported here into Login.fxml
            String css = this.getClass().getResource("./css/login.css").toExternalForm();
            scene.getStylesheets().add(css);
            primaryStage.setTitle("Login");
            primaryStage.setScene(scene);
            primaryStage.show();
            primaryStage.setMinWidth(400);
            primaryStage.setMinHeight(300);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeLocale(Locale locale) {
        showMainScene(locale);
    }

    public void showMainScene(Locale locale) {
        try {
            if (locale == null) {
                locale = Locale.getDefault();
            }
            ResourceBundle bundle = ResourceBundle.getBundle("locale.messages", locale);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openjfx/MasterView.fxml"));
            Parent root = loader.load();

            MasterController controller = loader.getController();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("./css/main.css").toExternalForm());
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