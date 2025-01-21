package org.openjfx.controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import launcher.Bot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import utils.GuildBaseInfo;
import utils.Helper;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GeneralController implements Initializable {
    private Bot botInstance;
    private boolean ready = false;
    private JDA jda;

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
    public Label uptimeLabel;
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
        botTokenLabel.setText(isKeyEntered("DISCORD_BOT_TOKEN") ?
                resourceBundle.getString("general.botLabel") :
                resourceBundle.getString("general.botPrompt"));
        apiKeyLabel.setText(isKeyEntered("OPENAI_API_KEY") ?
                resourceBundle.getString("general.gptLabel") :
                resourceBundle.getString("general.gptPrompt"));

        setupServerInfoTable();

        // Überprüfe, ob der Bot bereits läuft, und lade ggf. Daten
        if (Bot.getInstance().isRunning()) {
            botInstance = Bot.getInstance();
            jda = botInstance.getJda();
            ready = true;
            reloadData();
            startButton.setText("Shutdown Bot");
        } else {
            resetStatus();
        }
    }

    private void setupUptimeUpdater() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    uptimeLabel.setText(Helper.getUptime());
                })
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private boolean isKeyEntered(String envKey) {
        String value = System.getenv(envKey);
        if (value == null || value.isEmpty()) {
            System.err.println("Environment variable " + envKey + " is not set or empty.");
            return false;
        }
        return true;
    }

    @FXML
    public void startBot(ActionEvent actionEvent) {
        if (ready) {
            // Bot stoppen
            startButton.setDisable(true); // Button blockieren
            shutdownBot();
        } else {
            // Bot starten
            startButton.setDisable(true); // Button blockieren
            startBotInstance();
        }
    }

    private synchronized void startBotInstance() {
        if (jda != null && jda.getStatus() != JDA.Status.SHUTDOWN) {
            Platform.runLater(() -> {
                showError("Bot Already Running", "The bot is already running. Please shut it down first.");
                startButton.setDisable(false); // Button freigeben
            });
            return;
        }

        toggleLoading(true);

        new Thread(() -> {
            try {
                try {
                    botInstance = Bot.getInstance();
                    botInstance.start();
                    jda = botInstance.getJda();

                    Platform.runLater(() -> {
                        ready = true;
                        startButton.setText("Shutdown Bot");
                        reloadData();
                        toggleLoading(false);
                        startButton.setDisable(false); // Button freigeben
                    });
                } catch (InvalidTokenException e) {
                    Platform.runLater(() -> {
                        toggleLoading(false);
                        startButton.setDisable(false); // Button freigeben
                        showError("Invalid Bot Token", "The provided bot token is invalid. Please check your configuration.");
                    });
                }
            } catch (Exception e) {
                Platform.runLater(() -> {
                    toggleLoading(false);
                    startButton.setDisable(false); // Button freigeben
                    showError("Error Starting Bot", "Failed to start the bot: " + e.getMessage());
                });
            }
        }).start();
    }

    private void shutdownBot() {
        toggleLoading(true);
        startButton.setDisable(true);

        new Thread(() -> {
            botInstance.shutdown();

            Platform.runLater(() -> {
                ready = false;
                startButton.setText("Start Bot");
                resetStatus();
                toggleLoading(false);
                startButton.setDisable(false); // Button freigeben
            });
        }).start();
    }

    private void reloadData() {
        if (jda != null && botInstance.isRunning()) {
            Platform.runLater(() -> {
                // Update bot information
                setupUptimeUpdater();

                botNameLabel.setText(jda.getSelfUser().getName());
                botStatusLabel.setText("Online");
                botNameLabel.setVisible(true);
                botStatusLabel.setVisible(true);
                profileImageView.setImage(new Image(jda.getSelfUser().getEffectiveAvatarUrl()));

                loadServerInfo();
            });
        } else {
            resetStatus(); // Reset the UI if the bot is not running
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
