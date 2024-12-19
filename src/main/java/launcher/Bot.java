package launcher;

import api.ChatGPT;

import javafx.application.Platform;
import listeners.CommandManagerListener;
import listeners.ReactionListener;

import commands.chat.*;
import commands.guild.Kick;
import commands.testing.ButtonTest;
import commands.testing.DropDownTest;
import commands.testing.EntityDDTest;

import listeners.ReadyListener;
import net.dv8tion.jda.api.entities.Guild;
import org.openjfx.MyApp;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


public class Bot {
    // JDA init variables
    private final String TOKEN;
    private final EnumSet<GatewayIntent> INTENTS;
    // bot instances
    private final JDABuilder bot;
    private final JDA jda;
    private final ChatGPT gpt;
    // bot cosmetic
    private final String avatarUrl;
    private final Color defaultColor = new Color(115, 169, 186);
    // music

    /*
    private final MusicHandler;
    private final AudioPlayerManager playerManager;
    private ConcurrentMap<String, AudioPlayerSendHandler> audioSendHandlers;
    private ConcurrentMap<String, TrackScheduler> trackSchedulers;
    */

    // lazy load singleton - don't change access modifier to public
    private Bot() {
        /* music

        LavalinkClient client = new LavalinkClient(696275723924799529L);
        this.playerManager = new DefaultAudioPlayerManager();
        this.musicHandler = new MusicHandler(playerManager, this);
        this.audioSendHandlers = new ConcurrentHashMap<>();
        this.trackSchedulers = new ConcurrentHashMap<>();
        */
        // init commands
        CommandManagerListener commandManagerListener = new CommandManagerListener(
                new CmdPing(),
                new CmdGpt(),
                new Kick(),
                new CmdGichtus(),
                new CmdPoll(),
                new CmdEmbed(),
                new CmdRollDice(),
                new CmdAssignTeams(),
                new ButtonTest(),
                new DropDownTest(),
                new EntityDDTest(),
                new CmdPollNew()
        );

        // get init variables
        this.INTENTS = createIntents();
        this.TOKEN = getAuthToken();
        // create instances
        this.gpt = ChatGPT.getInstance();
        this.bot = JDABuilder
                .createDefault(TOKEN)
                .enableIntents(INTENTS)
                .addEventListeners(new ReactionListener(), commandManagerListener);

        // set jda instance for later use
        this.jda = bot.build();
        this.avatarUrl = jda.getSelfUser().getAvatarUrl();

        //initSlashCommands(jda.getGuilds());
    }

    public static EnumSet<GatewayIntent> createIntents() {
        return EnumSet.of(
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT
        );
    }

    public static String getAuthToken() {
        return System.getenv("DISCORD_BOT_TOKEN");
    }

    public JDA getJda() {
        return jda;
    }

    public Color getDefaultColor() {
        return defaultColor;
    }

    public List<String> getServerListName() {
        return this.getJda().getGuilds().stream().map(Guild::getName).collect(Collectors.toList());
    }


    // lazy load singleton
    private static final class InstanceHolder {
        private static final Bot instance = new Bot();
    }

    // lazy load singleton
    public static Bot getInstance() {
        return Bot.InstanceHolder.instance;
    }

    public JDABuilder getBot() {
        return bot;
    }

    public ChatGPT getGpt() {
        return gpt;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    /*
    public MusicHandler getMusicHandler() {
        return musicHandler;
    }


    public TrackScheduler getTrackScheduler(String guildId) {
        return trackSchedulers.computeIfAbsent(guildId, id -> createNewTrackScheduler(id));
    }

    public AudioSendHandler getAudioSendHandler(String guildId) {
        return audioSendHandlers.computeIfAbsent(guildId, id -> {
            TrackScheduler scheduler = getTrackScheduler(id);
            return new AudioPlayerSendHandler(scheduler.getPlayer());
        });
    }

    private TrackScheduler createNewTrackScheduler(String guildId) {
        AudioPlayer player = playerManager.createPlayer();
        TrackScheduler scheduler = new TrackScheduler(player, playerManager);
        player.addListener(scheduler);
        trackSchedulers.put(guildId, scheduler);
        return scheduler;
    }
     */



}
