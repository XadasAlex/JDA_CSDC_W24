package org.openjfx.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import org.openjfx.services.GuildService;
import utils.ChatMessage;

import java.net.URL;
import java.util.ResourceBundle;

public class MemberManagementController implements Initializable {

    private JDA jda;
    private GuildService guildService;
    private Member selectedMember;
    private boolean showGuildList = true;
    private ResourceBundle bundle;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources; // ResourceBundle speichern
    }

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

        memberTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                confirmMemberSelection(newValue);
            }
        });
    }

    public void toggleGuildList() {
        if (selectedMember == null) return;

        showGuildList = !showGuildList;

        String newText = bundle.getString(showGuildList ? "management.showChat" : "management.showGuilds");
        String newTitle = bundle.getString(showGuildList ? "management.selectGuild" : "management.chatHistory");

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
            showAlert("alert.noGuildSelected", "alert.noGuildSelectedContent");
            return;
        }

        String guildId = guildService.extractGuildIdFromSelection(selectedGuild);
        Guild guild = jda.getGuildById(guildId);

        if (guild == null) {
            showAlert("alert.guildNotFound", "alert.guildNotFoundContent");
            return;
        }

        new Thread(() -> {
            guild.loadMembers().onSuccess(members -> {
                Platform.runLater(() -> {
                    memberTableView.getItems().setAll(members.stream().filter(x -> !x.getUser().isBot()).toList());
                });
            }).onError(error -> {
                Platform.runLater(() -> {
                    showAlert("alert.errorLoadingMembers", bundle.getString("alert.errorMessage") + error.getMessage());
                });
            });
        }).start();
    }

    private void setupMemberTable() {
        memberName.setCellValueFactory(cellData -> {
            Member member = cellData.getValue();
            return new SimpleStringProperty(member.getUser().getName());
        });
        memberNickname.setCellValueFactory(cellData -> {
            Member member = cellData.getValue();
            return new SimpleStringProperty(
                    member.getNickname() != null ? member.getNickname() : bundle.getString("management.noNickname")
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
            showAlert("alert.noMemberSelected", "alert.noMemberSelectedContent");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(bundle.getString("dialog.sendMessageTitle"));
        dialog.setHeaderText(String.format(bundle.getString("dialog.sendMessageHeader"), selectedMember.getUser().getName()));
        dialog.setContentText(bundle.getString("dialog.sendMessagePrompt"));

        dialog.showAndWait().ifPresent(text -> {
            selectedMember.getUser().openPrivateChannel()
                    .queue(channel -> channel.sendMessage(text).queue());
        });
    }

    @FXML
    private void changeNickname() {
        if (selectedMember == null) {
            showAlert("alert.noMemberSelected", "alert.noMemberSelectedContent");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(bundle.getString("dialog.changeNicknameTitle"));
        dialog.setHeaderText(String.format(bundle.getString("dialog.changeNicknameHeader"), selectedMember.getUser().getName()));
        dialog.setContentText(bundle.getString("dialog.changeNicknamePrompt"));

        dialog.showAndWait().ifPresent(newNickname -> {
            if (newNickname.trim().isEmpty()) {
                Platform.runLater(() ->
                        showAlert("alert.nicknameChangeFailed", "alert.nicknameChangeFailedContentEmpty")
                );
                return;
            }

            try {
                selectedMember.modifyNickname(newNickname).queue(
                        success -> Platform.runLater(() ->
                                showAlert("alert.nicknameChanged", "alert.nicknameChangedContent")
                        ),
                        error -> Platform.runLater(() -> {
                            if (error instanceof HierarchyException) {
                                showAlert("alert.nicknameChangeFailed", "alert.nicknameFailure1");
                            } else if (error instanceof InsufficientPermissionException) {
                                showAlert("alert.nicknameChangeFailed", "alert.nicknameChangeFailedPermission");
                            } else {
                                showAlert("alert.nicknameChangeFailed", "alert.nicknameChangeFailedUnknown");
                                error.printStackTrace(); // Optional: Fehler im Log anzeigen
                            }
                        })
                );
            } catch (Exception e) {
                Platform.runLater(() ->
                        showAlert("alert.nicknameChangeFailed", "alert.nicknameChangeFailedUnknown")
                );
                e.printStackTrace();
            }
        });
    }


    @FXML
    private void kickMember() {
        if (selectedMember == null) {
            showAlert("alert.noMemberSelected", "alert.noMemberSelectedContent");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle(bundle.getString("dialog.kickMemberTitle"));
        confirmDialog.setHeaderText(String.format(bundle.getString("dialog.kickMemberHeader"), selectedMember.getUser().getName()));
        confirmDialog.setContentText(bundle.getString("dialog.kickMemberPrompt"));

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                selectedMember.getGuild().kick(selectedMember).queue();
                showAlert("alert.memberKicked", bundle.getString("alert.memberKickedContent"));
            }
        });
    }

    @FXML
    private void disconnect() {
        if (selectedMember == null) {
            showAlert("alert.noMemberSelected", "alert.noMemberSelectedContent");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle(bundle.getString("dialog.disconnectMemberTitle"));
        confirmDialog.setHeaderText(String.format(bundle.getString("dialog.disconnectMemberHeader"), selectedMember.getUser().getName()));
        confirmDialog.setContentText(bundle.getString("dialog.disconnectMemberPrompt"));

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                selectedMember.getGuild().moveVoiceMember(selectedMember, null).queue();
                showAlert("alert.memberDisconnected", bundle.getString("alert.memberDisconnectedContent"));
            }
        });
    }

    private void showAlert(String titleKey, String messageKey) {
        String title = bundle.getString(titleKey);
        String message = bundle.getString(messageKey);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void confirmMemberSelection(Member selectedMember) {
        if (selectedMember == null) {
            showAlert("alert.noMemberSelected", "alert.noMemberSelectedContent");
            return;
        }

        this.selectedMember = selectedMember;
        selectedMemberLabel.setText(String.format(bundle.getString("management.selectedMemberLabel"), selectedMember.getUser().getName()));
        memberManagementPane.setVisible(true);
    }
}
