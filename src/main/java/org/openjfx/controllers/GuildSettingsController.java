package org.openjfx.controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import net.dv8tion.jda.api.JDA;
import org.openjfx.services.GuildService;
import utils.GuildSettings;
import utils.Helper;

import java.util.Timer;

public class GuildSettingsController {
    private GuildService guildService;
    private JDA jda;
    private GuildSettings currentSettings;

    private final String trueColor = "-fx-border-color: #70db70;-fx-border-radius: 5.0";
    private final String falseColor = "-fx-border-color: #ff6666;-fx-border-radius: 5.0";

    private Timeline activeUpdater;

    @FXML
    public Label guildSettingsTitle;
    @FXML
    public ListView<String> guildListView;
    @FXML
    public Label guildSettingsSelected;
    @FXML
    private ToggleButton allowSpamToggle;
    @FXML
    private ToggleButton allowSwearToggle;
    @FXML
    private ToggleButton welcomeMessageToggle;
    @FXML
    private ToggleButton leaveMessageToggle;
    @FXML
    private ToggleButton allowMusicToggle;
    //@FXML
    //private ToggleButton dedicatedBotChannelsToggle;

    @FXML
    private Button saveSettingsButton;

    public void initializeData(JDA jda) {
        this.jda = jda;
        guildService = new GuildService(jda);
        guildService.loadGuildsIntoListView(guildListView);

        // Add a listener to load settings when a guild is selected
        guildListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                guildSettingsSelected.setText(String.format("Guild Settings for: %s", newValue.substring(0, newValue.indexOf("("))));
                loadSettings(newValue);
            }
        });
    }

    private void loadSettings(String selectedGuild) {
        String guildId = guildService.extractGuildIdFromSelection(selectedGuild);

        // Load settings for the selected guild asynchronously
        if (activeUpdater != null) {
            activeUpdater.stop();
        }

        activeUpdater = new Timeline(
                new KeyFrame(Duration.seconds(0.5), event -> {
                    currentSettings = GuildSettings.load(guildId);
                    Platform.runLater(() -> displaySettings(currentSettings));
                })
        );
        activeUpdater.setCycleCount(Animation.INDEFINITE);
        activeUpdater.play();
    }

    private void displaySettings(GuildSettings settings) {
        if (settings == null) {
            showAlert("Error", "Failed to load settings for the selected guild.");
            return;
        }

        // Update UI toggles based on the settings
        allowSpamToggle.setSelected(settings.isAllowSpam());
        allowSpamToggle.setStyle(settings.isAllowSpam() ? trueColor : falseColor);
        allowSwearToggle.setSelected(settings.isAllowSwear());
        allowSwearToggle.setStyle(settings.isAllowSwear() ? trueColor : falseColor);
        welcomeMessageToggle.setSelected(settings.isMemberWelcomeMessage());
        welcomeMessageToggle.setStyle(settings.isMemberWelcomeMessage() ? trueColor : falseColor);
        leaveMessageToggle.setSelected(settings.isMemberLeaveMessage());
        leaveMessageToggle.setStyle(settings.isMemberLeaveMessage() ? trueColor : falseColor);
        allowMusicToggle.setSelected(settings.isAllowMusic());
        allowMusicToggle.setStyle(settings.isAllowMusic() ? trueColor : falseColor);
        // dedicatedBotChannelsToggle.setSelected(settings.isDedicatedBotChannels());
        // dedicatedBotChannelsToggle.setStyle(settings.isDedicatedBotChannels() ? trueColor : falseColor);

        // Enable save button after settings are loaded
        saveSettingsButton.setDisable(false);
    }

    @FXML
    private void saveSettings() {
        if (currentSettings == null) {
            showAlert("No Settings Loaded", "Please select a guild to load settings first.");
            return;
        }

        // Update settings based on UI toggles
        currentSettings.setAllowSpam(allowSpamToggle.isSelected());
        currentSettings.setAllowSwear(allowSwearToggle.isSelected());
        currentSettings.setMemberWelcomeMessage(welcomeMessageToggle.isSelected());
        currentSettings.setMemberLeaveMessage(leaveMessageToggle.isSelected());
        currentSettings.setAllowMusic(allowMusicToggle.isSelected());
        //currentSettings.setDedicatedBotChannels(dedicatedBotChannelsToggle.isSelected());

        // Save updated settings to file
        currentSettings.update();

        showAlert("Success", "Guild settings have been saved successfully.");
    }

    public void toggleColor(ActionEvent actionEvent) {
        if (actionEvent.getSource() instanceof ToggleButton) {
            ToggleButton button = (ToggleButton) actionEvent.getSource();
            String style = button.isSelected() ? trueColor : falseColor;
            button.setStyle(style);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
