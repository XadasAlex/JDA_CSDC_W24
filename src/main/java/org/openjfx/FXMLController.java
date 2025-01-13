package org.openjfx;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;
import net.dv8tion.jda.api.JDA;
import launcher.Bot;
import stats.MemberStats;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.Locale;
import java.util.ResourceBundle;

public class FXMLController implements Initializable {

    private JDA jda;
    private Bot botInstance;
    private String statusText = "undefined.";
    private String serverList = "";
    private String loggedIn = "";
    private String profileImageURL = "";
    private boolean running = false;
    private Stage primaryStage;

    private static final int MAX_VISIBLE_ROWS = 10;
    private static final double EXTRA_PADDING = 2.0;

    private boolean isRankingTableInitialized = false;

    private List<Pane> allPanes;
    @FXML
    private ResourceBundle resources;
    @FXML
    private ComboBox<String> localeComboBox;
    //sortiere ich noch
    @FXML
    private Label statusL;
    @FXML
    private Label serverListL;
    @FXML
    private Label sessionL;
    @FXML
    private ImageView profileIV;
    @FXML
    private Button startBTN;
    // Stack pane for dynamic menus
    @FXML
    private GridPane allgemeinPane;
    @FXML
    private VBox sprachePane;
    @FXML
    private VBox featuresPane;
    @FXML
    private VBox firstPane;
    @FXML
    private VBox aboutPane;
    @FXML
    private TextField gptApikey;
    @FXML
    private TextField botToken;
    @FXML
    private ComboBox<String> statusCombo;
    @FXML
    private ToggleButton featuresToggle;
    @FXML
    private VBox featuresMenu;
    @FXML
    private ToggleButton statsToggle;
    @FXML
    private VBox statsMenu;
    @FXML
    private ListView<String> guildListView;
    @FXML
    private TableView<MemberStats> rankingTableView;

    //Stats list
    @FXML
    private TableColumn<MemberStats, String> memberIdColumn;
    @FXML
    private TableColumn<MemberStats, Integer> expColumn;
    @FXML
    private TableColumn<MemberStats, Integer> messagesSentColumn;
    @FXML
    private TableColumn<MemberStats, Integer> otherReactionCountColumn;
    @FXML
    private TableColumn<MemberStats, Integer> selfReactionCountColumn;
    @FXML
    private TableColumn<MemberStats, Integer> botInteractionsColumn;
    @FXML
    private TableColumn<MemberStats, Long> totalTimeColumn;
    @FXML
    private TableColumn<MemberStats, Long> lastTimeJoinedColumn;
    @FXML
    private TableColumn<MemberStats, Long> charactersSentColumn;
    @FXML
    private TableColumn<MemberStats, Boolean> inVoiceColumn;

    @FXML
    private VBox rankingPane;
    @FXML
    private Label guildTitle;


    private MyApp myApp;

    public void setMyApp(MyApp myApp) {
        this.myApp = myApp;
    }

    @FXML
    private void startBot() {
        String translatedTitleon;
        String translatedTitleoff;
        translatedTitleon = resources.getString("bot.start");
        translatedTitleoff = resources.getString("bot.stop");

        if (!running) {
            botInstance = Bot.getInstance();
            jda = botInstance.getJda();
            running = !running;
            startBTN.setText(translatedTitleoff);
        } else {
            Bot.getInstance().getJda().shutdown();
            startBTN.setText(translatedTitleon);
        }
        setupTimeline();
    }

    public void setResourceBundle(ResourceBundle resources) {
        this.resources = resources;
    }

    private static final Locale[] SUPPORTED_LOCALES = {
            Locale.of("en", "US"),
            Locale.of("de", "AT")
    };

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Do not initialize the bot here, as it will be done after the button click.
        // We only initialize UI components, not bot components.
        allPanes = Arrays.asList(firstPane, allgemeinPane, sprachePane, featuresPane, aboutPane, rankingPane);

        // Listener für Guild-Auswahl in der ListView
        guildListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) { // Wenn eine Guild-ID ausgewählt wird
                showRankingForGuild(newValue); // Guild-Rangliste anzeigen
            } else {
                resetRankingPane(); // Ranglisten-Pane ausblenden
            }
        });

        guildListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(String guildId, boolean empty) {
                super.updateItem(guildId, empty);
                if (empty || guildId == null) {
                    setText(null);
                } else {
                    // Wenn möglich, hol den Namen aus JDA
                    if (jda != null) {
                        var guild = jda.getGuildById(guildId);
                        if (guild != null) {
                            setText(guild.getName());
                        } else {
                            setText(guildId);
                        }
                    } else {
                        setText(guildId);
                    }
                }
            }
        });

        // Listener für Auswahl
        guildListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showRankingForGuild(newVal); // Das "newVal" ist die Guild-ID
            } else {
                resetRankingPane();
            }
        });
    }

    private void setupTimeline() {
        jda = Bot.getInstance().getJda(); // Now the bot is available

        Timeline tl = new Timeline(
                new KeyFrame(Duration.seconds(1.5), e -> {
                    if (jda != null) {
                        statusText = jda.getStatus().toString();
                        serverList = botInstance.getServerListName().toString();
                        loggedIn = String.format("Logged in as: %s#%s",
                                jda.getSelfUser().getName(),
                                jda.getSelfUser().getDiscriminator()
                        );
                        profileImageURL = jda.getSelfUser().getEffectiveAvatarUrl();

                        // setting values
                        sessionL.setText(loggedIn);
                        statusL.setText(statusText);
                        serverListL.setText(serverList);

                        // setting images
                        Image profileImage = new Image(profileImageURL);
                        profileIV.setImage(profileImage);
                    }

                }));
        tl.setCycleCount(Animation.INDEFINITE);
        tl.play();
    }

    //method for setting one pane visible and to the front and all others invisible
    private void showPane(Pane paneToShow) {
        for (Pane pane : allPanes) {
            boolean isTarget = (pane == paneToShow);
            pane.setVisible(isTarget);
            if (isTarget) {
                pane.toFront();
            }
        }
    }

    @FXML
    private void showAllgemein() {
        showPane(allgemeinPane);

        statusCombo.setValue("Online");
        statusCombo.setOnAction(event -> {
            String selectedStatus = statusCombo.getValue();
            System.out.println("User selected: " + selectedStatus);
        });
    }

    @FXML
    private void showSprache() {
        showPane(sprachePane);

        localeComboBox.getItems().addAll("English", "Deutsch");

        localeComboBox.setOnAction(event -> changeLocale());
        // Hier ggf. weitere Aktionen
    }
    @FXML
    private void changeLocale() {
        String selectedLanguage = localeComboBox.getValue();
        Locale selectedLocale = selectedLanguage.equals("English") ? Locale.of("en", "US") : Locale.of("de", "AT");

        // Anwendung mit der neuen Locale neu laden
        myApp.changeLocale(selectedLocale);
    }

    @FXML
    private void showAbout() {
        showPane(aboutPane);
    }

    //Dropdown menu für Features, commands...
    @FXML
    public void toggleFeaturesMenu() {
        showPane(featuresPane);

        boolean isExpanded = featuresToggle.isSelected();
        featuresMenu.setVisible(isExpanded);
        featuresMenu.setManaged(isExpanded);
    }

    //Chat-Gpt Api key
    @FXML
    public void getGptApikey() {
        String userInput = gptApikey.getText();
        System.out.println("User typed: " + userInput);
        //need to set Api key maybe save?
    }

    //Eingabe des bot Tokens sowohl First-pane als auch Allgemein
    @FXML
    public void getBotToken() {
        String userInput = botToken.getText();
        System.out.println("User typed: " + userInput);
    }

    public static List<String> getAllGuildIds() {
        // Pfad zum "guilds"-Ordner
        File guildsRoot = new File("src/main/resources/guilds");

        if (!guildsRoot.exists() || !guildsRoot.isDirectory()) {
            return List.of();
        }
        // Liste aller Unterordner, die numerisch aussehen (optional Filter)
        File[] guildFolders = guildsRoot.listFiles(f -> f.isDirectory());
        if (guildFolders == null) {
            return List.of();
        }

        // Namen der Ordner sind Guild-IDs
        return Arrays.stream(guildFolders)
                .map(File::getName)
                .collect(Collectors.toList());
    }

    public void initStatsGuilds() {
        List<String> guildIds = getAllGuildIds();

        // Wir wollen in jedem Fall IDs eintragen,
        // denn nur mit ID können wir später jda.getGuildById(...) aufrufen.
        guildListView.getItems().setAll(guildIds);
        adjustListViewHeight(guildListView);

        guildListView.refresh();
    }

    public void toggleStatsmenu() {

        boolean isExpanded = statsToggle.isSelected();
        statsMenu.setVisible(isExpanded);
        statsMenu.setManaged(isExpanded);
        if (isExpanded) {
            initStatsGuilds();
        }
    }

    private void adjustListViewHeight(ListView<?> listView) {
        // Zeilenhöhe pro Eintrag
        listView.setFixedCellSize(24.0);
        // Anzahl der Einträge holen
        int itemCount = listView.getItems().size();
        // Maximale Zeilen die angezeigt werden
        int visibleRows = Math.min(itemCount, MAX_VISIBLE_ROWS);
        //Neue Höhe berechnen
        double newHeight = visibleRows * listView.getFixedCellSize() + EXTRA_PADDING;
        // setze Grenzen
        listView.setPrefHeight(newHeight);
        listView.setMinHeight(Region.USE_PREF_SIZE);
        listView.setMaxHeight(Region.USE_PREF_SIZE);
    }

    private void initializeRankingTable() {
        if (isRankingTableInitialized) return; // Verhindert doppelte Initialisierung

        // Konfiguriere die Spalten
        memberIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMemberId()));
        expColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getExp()).asObject());
        messagesSentColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getMessagesSent()).asObject());
        otherReactionCountColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getOtherReactionCount()).asObject());
        selfReactionCountColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSelfReactionCount()).asObject());
        botInteractionsColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getBotInteractions()).asObject());
        totalTimeColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getTotalTime()));
        lastTimeJoinedColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getLastTimeJoined()));
        charactersSentColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCharactersSent()));
        inVoiceColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isInVoice()).asObject());

        // Bearbeitbare Spalten
        expColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        messagesSentColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        // Listener für Änderungen
        expColumn.setOnEditCommit(event -> {
            MemberStats member = event.getRowValue();
            member.setExp(event.getNewValue());
            member.updateSelf(); // Änderungen speichern
        });

        messagesSentColumn.setOnEditCommit(event -> {
            MemberStats member = event.getRowValue();
            member.setMessagesSent(event.getNewValue());
            member.updateSelf(); // Änderungen speichern
        });

        rankingTableView.setEditable(true);

        isRankingTableInitialized = true; // Initialisierung abgeschlossen
    }

    private void showRankingForGuild(String guildId) {
        // Tabelle initialisieren (falls noch nicht erfolgt)

        initializeRankingTable();

        String name;
        // Titel setzen
        try {
            name = jda.getGuildById(guildId).getName();
        }
        catch (Exception e) {
            name = guildId;
        }
        // Lokalisierter Titel
        String translatedTitle;
        translatedTitle = resources.getString("stats.guildTitle");
        guildTitle.setText(translatedTitle.replace("{0}", name));

        // Daten laden
        List<MemberStats> topMembers = MemberStats.topMembers(guildId);

        // Wenn keine Daten vorhanden sind
        if (topMembers == null || topMembers.isEmpty()) {
            rankingTableView.getItems().clear();
            rankingPane.setVisible(true);
            return;
        }

        // Daten in die Tabelle laden
        rankingTableView.getItems().setAll(topMembers);

        // Pane sichtbar machen
        showPane(rankingPane);
    }

    private void resetRankingPane() {
        rankingTableView.getItems().clear(); // Tabelle leeren
        guildTitle.setText("Guild Ranking"); // Titel zurücksetzen
        rankingPane.setVisible(false); // Pane ausblenden
    }
}