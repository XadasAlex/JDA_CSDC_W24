package org.openjfx;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import net.dv8tion.jda.api.JDA;
import launcher.Bot;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class FXMLController implements Initializable {

    private JDA jda;
    private Bot botInstance;
    private String statusText = "undefined.";
    private String serverList = "";
    private String loggedIn = "";
    private String profileImageURL = "";
    private boolean running = false;

    private static final int MAX_VISIBLE_ROWS = 10;
    private static final double EXTRA_PADDING = 2.0;

    private List<Pane> allPanes;
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
    private void startBot() {
        if (!running) {
            botInstance = Bot.getInstance();
            jda = botInstance.getJda();
            running = !running;
            startBTN.setText("Shutdown Bot");
        } else {
            Bot.getInstance().getJda().shutdown();
            startBTN.setText("Start Bot");
        }

        setupTimeline();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Do not initialize the bot here, as it will be done after the button click.
        // We only initialize UI components, not bot components.
        allPanes = Arrays.asList(firstPane, allgemeinPane, sprachePane, featuresPane, aboutPane);
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
        // Hier ggf. weitere Aktionen
    }

    @FXML
    private void showFeatures() {
        showPane(featuresPane);
        gptApikey.setText("gespeicherter Key später schon angeben");
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
        guildListView.getItems().setAll(guildIds);
        // Nun die Höhe anpassen
        adjustListViewHeight(guildListView);
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
}
