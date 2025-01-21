package org.openjfx.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import net.dv8tion.jda.api.JDA;
import org.openjfx.services.GuildService;
import stats.MemberStats;
import utils.Helper;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class RankingController implements Initializable {

    @FXML private ListView<String> guildListView;
    @FXML private TableView<MemberStats> rankingTableView;
    @FXML private TableColumn<MemberStats, String> memberNameColumn;
    @FXML private TableColumn<MemberStats, Integer> expColumn;
    @FXML private TableColumn<MemberStats, Integer> messagesSentColumn;
    @FXML private TableColumn<MemberStats, Long> charactersSentColumn;
    @FXML private TableColumn<MemberStats, String> totalTimeColumn;
    @FXML private TableColumn<MemberStats, Boolean> inVoiceColumn;
    @FXML private TableColumn<MemberStats, String> lastTimeJoinedColumn;

    private GuildService guildService;
    private JDA jda;
    private ResourceBundle bundle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources; // ResourceBundle speichern
    }

    public void initializeData(JDA jda) {
        this.guildService = new GuildService(jda);
        this.jda = jda;
        guildService.loadGuildsIntoListView(guildListView);
        setupRankingTable();

        guildListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                showRankingsForSelectedGuild(newValue);
            }
        });
    }

    private void setupRankingTable() {
        memberNameColumn.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        expColumn.setCellValueFactory(new PropertyValueFactory<>("exp"));
        messagesSentColumn.setCellValueFactory(new PropertyValueFactory<>("messagesSent"));
        charactersSentColumn.setCellValueFactory(new PropertyValueFactory<>("charactersSent"));
        totalTimeColumn.setCellValueFactory(cellData -> {
            MemberStats memberStats = cellData.getValue();
            long totalTime = memberStats.getTotalTime();
            String totalTimeString = Helper.formatSeconds(totalTime);
            return new SimpleStringProperty(totalTimeString);
        });
        inVoiceColumn.setCellValueFactory(new PropertyValueFactory<>("inVoice"));
        lastTimeJoinedColumn.setCellValueFactory(cellData -> {
            MemberStats memberStats = cellData.getValue();
            long lastTimeJoined = memberStats.getLastTimeJoined();
            if (lastTimeJoined == 0) {
                return new SimpleStringProperty("N/A");
            }
            return new SimpleStringProperty(Helper.getTimeAgo(lastTimeJoined));
        });

        rankingTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @FXML
    private void showRankingsForSelectedGuild(String selectedGuild) {
        if (selectedGuild == null) {
            showAlert("alert.noGuildSelected", "alert.noGuildSelectedContent");
            return;
        }

        String guildId = guildService.extractGuildIdFromSelection(selectedGuild);

        new Thread(() -> {
            List<MemberStats> memberStats = MemberStats.topMembers(guildId);
            if (memberStats == null || memberStats.isEmpty()) {
                Platform.runLater(() -> showAlert("alert.noData", "alert.noDataContent"));
                return;
            }

            memberStats.sort(Comparator.comparingInt(MemberStats::getExp).reversed());

            Platform.runLater(() -> rankingTableView.getItems().setAll(memberStats));
        }).start();
    }

    private void showAlert(String titleKey, String messageKey, Object... params) {
        String title = bundle.getString(titleKey);
        String message = formatMessage(bundle.getString(messageKey), params);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String formatMessage(String message, Object... params) {
        return params.length > 0 ? String.format(message, params) : message;
    }
}
