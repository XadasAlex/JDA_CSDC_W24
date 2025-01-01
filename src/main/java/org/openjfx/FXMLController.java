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
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import net.dv8tion.jda.api.JDA;
import launcher.Bot;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class FXMLController implements Initializable {

    private JDA jda;
    private Bot botInstance;
    private String statusText = "undefined.";
    private String serverList = "";
    private String loggedIn = "";
    private String profileImageURL = "";
    private boolean running = false;

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

    @FXML
    private void showAllgemein() {
        firstPane.setVisible(false);
        allgemeinPane.setVisible(true);
        sprachePane.setVisible(false);
        featuresPane.setVisible(false);
        aboutPane.setVisible(false);
        allgemeinPane.toFront();
        //Hier dann was es in echt is setzen
        statusCombo.setValue("Online");
        statusCombo.setOnAction(event -> {
            String selectedStatus = statusCombo.getValue();
            System.out.println("User selected: " + selectedStatus);
        });
    }

    @FXML
    private void showSprache() {
        firstPane.setVisible(false);
        allgemeinPane.setVisible(false);
        sprachePane.setVisible(true);
        featuresPane.setVisible(false);
        aboutPane.setVisible(false);
        sprachePane.toFront();
    }

    @FXML
    private void showFeatures() {
        firstPane.setVisible(false);
        allgemeinPane.setVisible(false);
        sprachePane.setVisible(false);
        featuresPane.setVisible(true);
        aboutPane.setVisible(false);
        featuresPane.toFront();
        gptApikey.setText("gespeicherter Key später schon angeben");
    }

    //About section
    @FXML
    private void showAbout() {
        aboutPane.setVisible(true);
        firstPane.setVisible(false);
        allgemeinPane.setVisible(false);
        sprachePane.setVisible(false);
        featuresPane.setVisible(false);
        aboutPane.toFront();
    }

    //Dropdown menu für Features, commands...
    @FXML
    public void toggleFeaturesMenu() {
        showFeatures();
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
}
