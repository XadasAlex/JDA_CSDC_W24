import api.SimpleHttpServer;
import bot.utils.ErrorHandler;
import bot.utils.MessageAnalyzer;
import handlers.CommandHandler;
import listeners.MessageListener;
import listeners.ReadyListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;


public class Launcher {


    public static void main(String[] args) throws InterruptedException {
        final String TOKEN = getAuthToken();
        final EnumSet<GatewayIntent> INTENTS = createIntents();
        final String COMMAND_PREFIX = "!";

        MessageAnalyzer ma = new MessageAnalyzer(COMMAND_PREFIX);
        CommandHandler ch = new CommandHandler();
        ErrorHandler eh = new ErrorHandler();



        JDA jda = JDABuilder
                .createDefault(TOKEN)
                .enableIntents(INTENTS)
                .addEventListeners(new MessageListener(ma, ch, eh))
                .addEventListeners(new ReadyListener())
                .build();

        Thread httpThread = new Thread(() -> {
            SimpleHttpServer server = new SimpleHttpServer(jda);
            try {
                server.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        httpThread.start();


        jda.awaitReady();

    }

    public static EnumSet<GatewayIntent> createIntents() {
        return EnumSet.of(
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT
        );
    }

    public static String getAuthToken() {
        String token = "n.d";

        try {
            Path path = Paths.get("src/main/resources/TOKEN.txt");
            token = Files.readAllLines(path).getFirst();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return token;
    }
}
