package org.openjfx.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import launcher.Bot;
import org.openjfx.MyApp;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MasterController implements Initializable {
    @FXML
    private StackPane contentPane;

    private ResourceBundle bundle;

    private MyApp myApp;

    public void setMyApp(MyApp myApp) {
        this.myApp = myApp;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        // Automatisch die allgemeine Ansicht laden
        showGeneral();

    }

    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            loader.setResources(bundle);  // Das ResourceBundle übergeben
            Pane view = loader.load();

            contentPane.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void showGeneral() {
        loadView("/org/openjfx/GeneralView.fxml");
    }

    @FXML
    private void showLanguage() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/openjfx/LanguageView.fxml")
            );
            loader.setResources(bundle); // Lokalisierungs-Bundle setzen
            Pane languagePane = loader.load();

            LanguageController languageCtrl = loader.getController();
            languageCtrl.setMyApp(myApp); // MyApp setzen

            contentPane.getChildren().setAll(languagePane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void showMemberManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openjfx/MemberManagementView.fxml"));
            loader.setResources(bundle);  // Lokalisierungs-Bundle setzen
            Pane memberManagementPane = loader.load();

            // Zugriff auf den Controller
            MemberManagementController controller = loader.getController();
            controller.initializeData(Bot.getInstance().getJda());

            contentPane.getChildren().setAll(memberManagementPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showRanking() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openjfx/RankingView.fxml"));
            loader.setResources(bundle);  // Lokalisierungs-Bundle setzen
            Pane rankingPane = loader.load();

            // Zugriff auf den Controller
            RankingController controller = loader.getController();
            controller.initializeData(Bot.getInstance().getJda());

            contentPane.getChildren().setAll(rankingPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void showServerSettings(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openjfx/GuildSettings.fxml"));
            loader.setResources(bundle);  // Lokalisierungs-Bundle setzen
            Pane settingsPane = loader.load();

            GuildSettingsController controller = loader.getController();
            controller.initializeData(Bot.getInstance().getJda());

            contentPane.getChildren().setAll(settingsPane);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void showAudioSettings(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openjfx/AudioSettingsView.fxml"));
            loader.setResources(bundle);  // Lokalisierungs-Bundle setzen
            Pane memberManagementPane = loader.load();

            AudioSettingsController controller = loader.getController();
            controller.initializeData();
            contentPane.getChildren().setAll(memberManagementPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
