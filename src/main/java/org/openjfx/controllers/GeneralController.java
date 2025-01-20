package org.openjfx.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import launcher.Bot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import utils.GuildBaseInfo;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GeneralController implements Initializable {
    private Bot botInstance = null;
    private boolean ready = false;
    private JDA jda;

    // Input fields
    @FXML
    private PasswordField botTokenTextField;
    @FXML
    private PasswordField apiKeyTextField;
    @FXML
    private Label botTokenLabel;
    @FXML
    private Label apiKeyLabel;

    // Bot information
    @FXML
    private ImageView profileImageView;
    @FXML
    private Label botNameLabel;
    @FXML
    private Label botStatusLabel;
    @FXML
    private TableView<GuildBaseInfo> serverTableView;
    @FXML
    private TableColumn<GuildBaseInfo, String> serverNameColumn;
    @FXML
    private TableColumn<GuildBaseInfo, String> totalMembersColumn;
    @FXML
    private TableColumn<GuildBaseInfo, String> onlineMembersColumn;

    // Buttons
    @FXML
    private Button startButton;

    // Loading
    @FXML
    private ProgressIndicator loadingSpinner;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        botTokenLabel.setVisible(isKeyEntered("DISCORD_BOT_TOKEN"));
        apiKeyLabel.setVisible(isKeyEntered("OPENAI_API_KEY"));
        setupServerInfoTable();
    }

    private boolean isKeyEntered(String envKey) {
        String value = System.getenv(envKey);
        return value != null && !value.isEmpty();
    }

    public void saveSettings(ActionEvent actionEvent) {
        System.out.println(botTokenTextField.getText());
        System.out.println(apiKeyTextField.getText());
    }

    @FXML
    public void startBot(ActionEvent actionEvent) {
        if (ready) {
            shutdownBot();
        } else {
            startBotInstance();
        }
    }

    private void startBotInstance() {
        toggleLoading(true);
        startButton.setDisable(true);

        new Thread(() -> {
            try {
                botInstance = Bot.getInstance();
                jda = botInstance.getJda();
                jda.awaitReady();

                Platform.runLater(() -> {
                    ready = true;
                    startButton.setText("Shutdown Bot");
                    updateStatus();
                    toggleLoading(false);
                    startButton.setDisable(false);
                });
            } catch (InterruptedException e) {
                Platform.runLater(() -> {
                    showError("Error Starting Bot", "Failed to start the bot: " + e.getMessage());
                    toggleLoading(false);
                    startButton.setDisable(false);
                });
            }
        }).start();
    }

    private void shutdownBot() {
        toggleLoading(true);
        startButton.setDisable(true);

        new Thread(() -> {
            if (jda != null) {
                jda.shutdown();
                botInstance = null;
                jda = null;
            }

            Platform.runLater(() -> {
                ready = false;
                startButton.setText("Start Bot");
                resetStatus();
                toggleLoading(false);
                startButton.setDisable(false);
            });
        }).start();
    }

    private void updateStatus() {
        if (jda != null) {
            Platform.runLater(() -> {
                botNameLabel.setText(jda.getSelfUser().getName());
                botStatusLabel.setText("Online");
                botNameLabel.setVisible(true);
                botStatusLabel.setVisible(true);
                profileImageView.setImage(new Image(jda.getSelfUser().getEffectiveAvatarUrl()));
                loadServerInfo();
            });
        }
    }

    private void resetStatus() {
        botNameLabel.setText("Bot Offline");
        botStatusLabel.setText("Offline");
        profileImageView.setImage(null);
        serverTableView.getItems().clear();
    }

    private void setupServerInfoTable() {
        serverNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getGuildName()));
        totalMembersColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTotalMembers()));
        onlineMembersColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getOnlineMembers()));
    }

    private void loadServerInfo() {
        List<GuildBaseInfo> guilds = jda.getGuilds().stream()
                .map(guild -> {
                    String guildName = guild.getName();
                    String memberCount = String.valueOf(guild.getMemberCount());
                    GuildBaseInfo info = new GuildBaseInfo(guildName, memberCount, "Loading...");

                    guild.loadMembers().onSuccess(members -> {
                        String onlineCount = String.valueOf((int) members.stream()
                                .filter(m -> m.getOnlineStatus() == OnlineStatus.ONLINE)
                                .count());
                        Platform.runLater(() -> {
                            info.setOnlineMemberCount(onlineCount);
                            serverTableView.refresh();
                        });
                    });

                    return info;
                }).toList();

        Platform.runLater(() -> serverTableView.getItems().setAll(guilds));
    }

    private void toggleLoading(boolean show) {
        Platform.runLater(() -> loadingSpinner.setVisible(show));
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
