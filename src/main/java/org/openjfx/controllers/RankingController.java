package org.openjfx.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import net.dv8tion.jda.api.JDA;
import org.openjfx.services.GuildService;
import stats.MemberStats;
import utils.Helper;

import java.util.Comparator;
import java.util.List;

public class RankingController {

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

    public void initializeData(JDA jda) {
        this.guildService = new GuildService(jda);
        this.jda = jda;
        guildService.loadGuildsIntoListView(guildListView);
        setupRankingTable();
    }

    private void setupRankingTable() {
        // Spalten für Member-Name und Exp (bereits vorhanden)
        memberNameColumn.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        expColumn.setCellValueFactory(new PropertyValueFactory<>("exp"));
        messagesSentColumn.setCellValueFactory(new PropertyValueFactory<>("messagesSent")); // Anzahl der gesendeten Nachrichten
        charactersSentColumn.setCellValueFactory(new PropertyValueFactory<>("charactersSent")); // Anzahl der gesendeten Zeichen
        totalTimeColumn.setCellValueFactory(cellData -> {
            MemberStats memberStats = cellData.getValue();
            long totalTime = memberStats.getTotalTime();
            String totalTimeString = Helper.formatSeconds(totalTime);
            return new SimpleStringProperty(totalTimeString);
        }); // Gesamtzeit im Voice-Channel
        inVoiceColumn.setCellValueFactory(new PropertyValueFactory<>("inVoice")); // Ob der Nutzer gerade im Voice ist
        lastTimeJoinedColumn.setCellValueFactory(cellData -> {
            MemberStats memberStats = cellData.getValue();
            long lastTimeJoined = memberStats.getLastTimeJoined();
            if (lastTimeJoined == 0) {
                return new SimpleStringProperty("N/A");
            }
            return new SimpleStringProperty(Helper.getTimeAgo(lastTimeJoined));
        }); // Letzter Voice-Beitritt formatiert

        // Optionale Anpassungen: Automatische Spaltenbreite
        rankingTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @FXML
    private void showRankingsForSelectedGuild() {
        String selectedGuild = guildListView.getSelectionModel().getSelectedItem();
        if (selectedGuild == null) {
            showAlert("No Guild Selected", "Please select a guild to view rankings.");
            return;
        }

        String guildId = guildService.extractGuildIdFromSelection(selectedGuild);

        new Thread(() -> {
            // Hole und sortiere die Mitgliederstatistiken
            List<MemberStats> memberStats = MemberStats.topMembers(guildId);
            if (memberStats == null || memberStats.isEmpty()) {
                Platform.runLater(() -> {
                    showAlert("No Data", "No member statistics found for this guild.");
                });
                return;
            }

            memberStats.sort(Comparator.comparingInt(MemberStats::getExp).reversed()); // Sortiere nach Exp (absteigend)

            // Aktualisiere die Tabelle
            Platform.runLater(() -> {
                rankingTableView.getItems().setAll(memberStats);
            });
        }).start();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
