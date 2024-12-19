package org.openjfx;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import net.dv8tion.jda.api.JDA;
import launcher.Bot;

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
                        profileImageURL = jda.getSelfUser().getAvatarUrl();

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
}
