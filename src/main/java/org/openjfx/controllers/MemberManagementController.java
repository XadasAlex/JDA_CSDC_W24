package org.openjfx.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.openjfx.services.GuildService;
import utils.ChatMessage;

public class MemberManagementController {

    private JDA jda;
    private GuildService guildService;
    private Member selectedMember;
    private boolean showGuildList = true;

    @FXML private ListView<String> guildListView;
    @FXML private TableView<Member> memberTableView;
    @FXML private TableColumn<Member, String> memberName;
    @FXML private TableColumn<Member, String> memberNickname;
    @FXML private TableColumn<Member, String> memberDiscriminator;
    @FXML private TableColumn<Member, String> memberId;
    @FXML private Button toggleChatGuildButton;
    @FXML private VBox memberManagementPane;
    @FXML private Label selectedMemberLabel;
    @FXML private Label memberTitle;
    @FXML private TableView<ChatMessage> chatTableView;
    @FXML private TableColumn<ChatMessage, String> timestampColumn;
    @FXML private TableColumn<ChatMessage, String> contentColumn;
    @FXML private TableColumn<ChatMessage, String> channelColumn;

    public void initializeData(JDA jda) {
        this.jda = jda;
        guildService = new GuildService(jda);
        guildService.loadGuildsIntoListView(guildListView); // Gilden in die Liste laden
        setupMemberTable();

        guildListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                showMembers(newValue);
            }
        });
    }

    public void toggleGuildList() {
        if (selectedMember == null) return;

        showGuildList = !showGuildList;

        String newText = showGuildList ? "Show Chat" : "Show Guilds";
        String newTitle = showGuildList ?
                "Select a Guild for Member Management" : "The Members chat history is listed below";

        guildListView.setVisible(showGuildList);
        guildListView.setManaged(showGuildList);

        chatTableView.setVisible(!showGuildList);
        chatTableView.setManaged(!showGuildList);

        toggleChatGuildButton.setText(newText);
        memberTitle.setText(newTitle);

        setupChatTable();
        Platform.runLater(() -> {
            loadChatHistory(selectedMember);
        });
    }

    private void setupChatTable() {
        timestampColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTimestamp()));
        contentColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getContent()));
        channelColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getChannelName()));
    }

    public void loadChatHistory(Member selectedMember) {
        guildService.loadMembersChatHistoryIntoTableView(chatTableView, selectedMember);
    }

    @FXML
    public void showMembers(String selectedGuild) {
        if (selectedGuild == null) {
            showAlert("No Guild Selected", "Please select a guild to view its members.");
            return;
        }

        String guildId = guildService.extractGuildIdFromSelection(selectedGuild);
        Guild guild = jda.getGuildById(guildId);

        if (guild == null) {
            showAlert("Guild Not Found", "The selected guild could not be found.");
            return;
        }

        // Lade Mitglieder asynchron
        new Thread(() -> {
            guild.loadMembers().onSuccess(members -> {
                Platform.runLater(() -> {
                    memberTableView.getItems().setAll(members); // Mitglieder in Tabelle anzeigen
                });
            }).onError(error -> {
                Platform.runLater(() -> {
                    showAlert("Error", "Failed to load members: " + error.getMessage());
                });
            });
        }).start();
    }

    private void setupMemberTable() {
        // Konfiguriere die Spalten für die Tabelle
        memberName.setCellValueFactory(cellData -> {
            Member member = cellData.getValue();
            return new SimpleStringProperty(member.getUser().getName());
        });
        memberNickname.setCellValueFactory(cellData -> {
            Member member = cellData.getValue();
            return new SimpleStringProperty(
                    member.getNickname() != null ? member.getNickname() : "N/A"
            );
        });
        memberDiscriminator.setCellValueFactory(cellData -> {
            Member member = cellData.getValue();
            return new SimpleStringProperty(member.getUser().getDiscriminator());
        });
        memberId.setCellValueFactory(cellData -> {
            Member member = cellData.getValue();
            return new SimpleStringProperty(member.getId());
        });
    }

    @FXML
    private void sendMessageToMember() {
        if (selectedMember == null) {
            showAlert("No Member Selected", "Please select a member first.");
            return;
        }

        // Beispiel: Nachricht an den ausgewählten Member senden
        selectedMember.getUser().openPrivateChannel()
                .queue(channel -> channel.sendMessage("Hello, " + selectedMember.getUser().getName()).queue());
    }

    @FXML
    private void changeNickname() {
        if (selectedMember == null) {
            showAlert("No Member Selected", "Please select a member first.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Change Nickname");
        dialog.setHeaderText("Change Nickname for " + selectedMember.getUser().getName());
        dialog.setContentText("Enter new nickname:");

        dialog.showAndWait().ifPresent(newNickname -> {
            selectedMember.modifyNickname(newNickname).queue();
            showAlert("Nickname Changed", "The nickname has been changed to " + newNickname + ".");
        });
    }

    @FXML
    private void kickMember() {
        if (selectedMember == null) {
            showAlert("No Member Selected", "Please select a member first.");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Kick Member");
        confirmDialog.setHeaderText("Are you sure you want to kick " + selectedMember.getUser().getName() + "?");
        confirmDialog.setContentText("This action cannot be undone.");

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                selectedMember.getGuild().kick(selectedMember).queue();
                showAlert("Member Kicked", "The member has been kicked from the guild.");
            }
        });
    }

    @FXML
    private void disconnect() {
        if (selectedMember == null) {
            showAlert("No Member Selected", "Please select a member first.");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Disconnect Member");
        confirmDialog.setHeaderText("Are you sure you want to disconnect " + selectedMember.getUser().getName() + "?");
        confirmDialog.setContentText("They might get mad. :(");

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                selectedMember.getGuild().moveVoiceMember(selectedMember, null).queue();
                showAlert("Member Disconnected!", "The member has been disconnected from their voice channel.");
            }
        });
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    private void confirmMemberSelection() {
        Member selected = memberTableView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("No Member Selected", "Please select a member first.");
            return;
        }

        // Speichere den ausgewählten Member und zeige die Verwaltungsschnittstelle
        this.selectedMember = selected;
        selectedMemberLabel.setText("Selected Member: " + selected.getUser().getName());
        memberManagementPane.setVisible(true);
        memberManagementPane.setManaged(true);
    }

}
