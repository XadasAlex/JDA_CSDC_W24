package org.openjfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.sqlite.JDBC;
import utils.Helper;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;



public class MyApp extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage stage) throws SQLException {
        //Create Table sql statements for when the DB does not exist anymore
        var createPlayersTable = "CREATE TABLE IF NOT EXISTS Players ("
                + "	PlayerID INTEGER PRIMARY KEY);";

        var createGamesTable = "CREATE TABLE IF NOT EXISTS Games ("
                + "	GameID INTEGER PRIMARY KEY,"
                + " PlayerOne INTEGER NOT NULL,"
                + " PlayerOneWins INTEGER,"
                + " PlayerTwoWins INTEGER,"
                + " PlayerTwo INTEGER NOT NULL,"
                + " FOREIGN KEY (PlayerOne) REFERENCES Players(PlayerID),"
                + " FOREIGN KEY (PlayerTwo) REFERENCES Players(PlayerID));";

        var createMovesTable = "CREATE TABLE IF NOT EXISTS Moves ("
                + "	MoveID INTEGER PRIMARY KEY,"
                + " Game INTEGER NOT NULL,"
                + " Moves TEXT,"
                + "	FOREIGN KEY (Game) REFERENCES Games(GameID));";


        DriverManager.registerDriver(new JDBC());

        //creation of the DB and execution of the create table statements
        try (var conn = DriverManager.getConnection(String.format("jdbc:sqlite:%s/battleships.db", Helper.getBaseDBPath()))) {
            if (conn != null) {
                conn.createStatement().execute(createPlayersTable);
                conn.createStatement().execute(createGamesTable);
                conn.createStatement().execute(createMovesTable);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        this.primaryStage = stage;
        showLogin();

    }

    public void showLogin() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();

            // Controller holen und MyApp übergeben
            LoginController loginCtrl = loader.getController();
            loginCtrl.setMyApp(this);

            Scene scene = new Scene(root, 400, 300); // z.B. 400x300
            primaryStage.setTitle("Login");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeLocale(Locale locale) {
        showMainScene(locale);
    }
    public void showMainScene(Locale locale) {
        try {

             if(locale == null) {
                 locale = Locale.getDefault();
             }

            ResourceBundle bundle = ResourceBundle.getBundle("locale.messages", locale);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("scene.fxml"), bundle);
            Parent root = loader.load();

            FXMLController controller = loader.getController();
            controller.setMyApp(this);  // Übergib die MyApp-Instanz

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
            primaryStage.setTitle("Discord-Bot");
            primaryStage.setScene(scene);
            primaryStage.show();

            // Mindestgröße
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(700);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}