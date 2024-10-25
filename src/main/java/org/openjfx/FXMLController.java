package org.openjfx;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import api.helper.JDAHelper;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import net.dv8tion.jda.api.JDA;

public class FXMLController implements Initializable {
    private JDA jda;
    private String statusText = "undefined.";
    private String serverList = "";
    private String loggedIn = "";
    private String profileImageURL = "";

    @FXML
    private Label statusL;
    @FXML
    private Label serverListL;
    @FXML
    private Label sessionL;
    @FXML
    private ImageView profileIV;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        Timeline tl = new Timeline(
                new KeyFrame(Duration.seconds(1.5), e -> {
                    if (jda != null) {
                        // grabbing values
                        statusText = jda.getStatus().toString();
                        serverList = Arrays.toString(JDAHelper.getServerListName());
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
                    } else {
                        jda = JDAHelper.getJDA();
                    }

                }));
        tl.setCycleCount(Animation.INDEFINITE);
        tl.play();


    }


}