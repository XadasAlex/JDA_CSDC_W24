package org.openjfx.controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.util.Duration;
import launcher.Bot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AudioSettingsController {
    @FXML
    private TextArea consoleOutput;

    private Process lavalinkProcess = null;
    private Timeline activeUpdater;
    private Thread outputThread;
    private Thread errorThread;

    public void initializeData() {
        if (activeUpdater != null || lavalinkProcess != null) {
            activeUpdater.stop();
        }

        activeUpdater = new Timeline(
                new KeyFrame(Duration.seconds(0.5), event -> {
                    Process process = Bot.getInstance().getLavaLinkProcess();

                    // Prüfen, ob sich der Prozess geändert hat
                    if (process != null && process != lavalinkProcess) {
                        lavalinkProcess = process;
                        startReadingStreams();
                    }
                })
        );
        activeUpdater.setCycleCount(Animation.INDEFINITE);
        activeUpdater.play();
    }

    private void startReadingStreams() {
        if (outputThread != null && outputThread.isAlive()) {
            outputThread.interrupt();
        }
        if (errorThread != null && errorThread.isAlive()) {
            errorThread.interrupt();
        }

        outputThread = new Thread(() -> readStream(lavalinkProcess.getInputStream(), false));
        errorThread = new Thread(() -> readStream(lavalinkProcess.getErrorStream(), true));

        outputThread.setDaemon(true);
        errorThread.setDaemon(true);

        outputThread.start();
        errorThread.start();
    }

    private void readStream(InputStream stream, boolean isError) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                final String log = (isError ? "[ERROR] " : "") + line + "\n";
                Platform.runLater(() -> consoleOutput.appendText(log));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
