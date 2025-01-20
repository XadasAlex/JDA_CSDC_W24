package org.openjfx.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import launcher.Bot;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MasterController implements Initializable {
    @FXML
    private StackPane contentPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Automatisch die allgemeine Ansicht laden
        showGeneral();
    }

    @FXML
    private void showGeneral() {
        loadView("/org/openjfx/GeneralView.fxml");
    }

    @FXML
    private void showLanguage() {
        loadView("/org/openjfx/LanguageView.fxml");
    }

    @FXML
    private void showFeatures() {
        loadView("/org/openjfx/FeaturesView.fxml");
    }

    @FXML
    private void showMemberManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openjfx/MemberManagementView.fxml"));
            Pane rankingPane = loader.load();

            // Zugriff auf den Controller
            MemberManagementController controller = loader.getController();
            controller.initializeData(Bot.getInstance().getJda());

            contentPane.getChildren().setAll(rankingPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showRanking() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openjfx/RankingView.fxml"));
            Pane rankingPane = loader.load();

            // Zugriff auf den Controller
            RankingController controller = loader.getController();
            controller.initializeData(Bot.getInstance().getJda());

            contentPane.getChildren().setAll(rankingPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Pane view = loader.load();
            contentPane.getChildren().setAll(view); // Dynamisches Ersetzen des Inhalts
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showServerSettings(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openjfx/GuildSettings.fxml"));
            Pane settingsPane = loader.load();

            GuildSettingsController controller = loader.getController();
            controller.initializeData(Bot.getInstance().getJda());

            contentPane.getChildren().setAll(settingsPane);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
