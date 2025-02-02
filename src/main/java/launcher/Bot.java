package launcher;

import api.ChatGPT;

import commands.battleship.*;
import commands.chat.stats.CmdAllowStats;
import commands.chat.stats.CmdLeaderBoard;
import commands.chat.stats.CmdStats;
import commands.guild.CmdGuildInfo;
import commands.guild.CmdJoin;
import commands.guild.CmdLeave;
import commands.guild.CmdSettingsChatRestricted;
import commands.music.CmdPause;
import commands.music.CmdPlay;
import commands.music.CmdResume;
import commands.music.CmdStop;
import dev.arbjerg.lavalink.client.*;
import dev.arbjerg.lavalink.client.event.EmittedEvent;
import dev.arbjerg.lavalink.client.event.ReadyEvent;
import dev.arbjerg.lavalink.client.event.StatsEvent;
import dev.arbjerg.lavalink.client.event.TrackStartEvent;
import dev.arbjerg.lavalink.client.loadbalancing.RegionGroup;
import dev.arbjerg.lavalink.client.loadbalancing.builtin.VoiceRegionPenaltyProvider;
import dev.arbjerg.lavalink.client.player.Track;
import dev.arbjerg.lavalink.libraries.jda.JDAVoiceUpdateListener;
import listeners.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import okhttp3.ResponseBody;
import utils.GuildSettings;

import commands.chat.*;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.*;
import java.util.List;


public class Bot {
    private JDABuilder bot;
    private JDA jda;
    private ChatGPT gpt;

    private final String avatarUrl = "https://example.com/default-avatar.png"; // Placeholder
    private final Color defaultColor = new Color(115, 169, 186);
    private final Color errorColor = new Color(255, 51, 51);
    private Instant startTime = null;
    private boolean running = false; // Neuer Status-Tracker
    private HashMap<String, GuildSettings> guildSettingsHashMap;
    private final LavalinkClient client;

    public Instant getStartTime() {
        return startTime;
    }

    public LavalinkClient getLavalinkClient() {
        return client;
    }

    private static final class InstanceHolder {
        private static final Bot instance = new Bot();
    }

    public static Bot getInstance() {
        return InstanceHolder.instance;
    }

    private Bot() {
        // Intents und GPT-Instanz vorbereiten
        EnumSet<GatewayIntent> INTENTS = createIntents();
        this.gpt = ChatGPT.getInstance();

        CommandManagerListener commandManagerListener = new CommandManagerListener(
                new CmdPing(),
                new CmdGpt(),
                //new CmdKick(),
                new CmdEmbed(),
                new CmdRollDice(),
                //new CmdAssignTeams(),
                //new ButtonTest(),
                //new DropDownTest(),
                //new EntityDDTest(),
                new CmdPoll(),
                new CmdAllowStats(),
                //new CmdHelper(),
                new CmdLeaderBoard(),
                new CmdStats(),
                new CmdGuildInfo(),
                new CmdBotSelfInviteLink(),
                new RegisterBattleships(),
                new BattleshipStartGame(),
                new SurrenderGame(),
                new SetShips(),
                new MakeMove(),
                new AcceptBattleshipGame(),
                //new CmdAnnoy(),
                new CmdSettingsChatRestricted(),
                new CmdPlay(),
                new CmdStop(),
                new CmdPause(),
                new CmdResume(),
                new CmdJoin(),
                new CmdLeave()
        );

        // JDABuilder vorbereiten, aber nicht starten
        String TOKEN = System.getenv("DISCORD_BOT_TOKEN");
        this.client = new LavalinkClient(
                Helpers.getUserIdFromToken(TOKEN)
        );
        // ??
        this.client.getLoadBalancer().addPenaltyProvider(new VoiceRegionPenaltyProvider());

        registerLavalinkListeners();
        registerLavalinkNodes();

        this.bot = JDABuilder
                .createDefault(TOKEN)
                .enableIntents(INTENTS)
                .enableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE, CacheFlag.ONLINE_STATUS)
                .setVoiceDispatchInterceptor(new JDAVoiceUpdateListener(client))
                .addEventListeners(
                        new ChatListener(),
                        new StatListener(),
                        commandManagerListener,
                        new ReadyListener(),
                        new JoinLeaveListener(),
                        new SpamListener()
                );
    }

    private void registerLavalinkNodes() {
        List.of(client.addNode(new NodeOptions.Builder()
                .setName("Local-Lavalink")
                .setServerUri(URI.create("ws://127.0.0.1:2333"))
                .setPassword("youshallnotpass")
                .setRegionFilter(RegionGroup.EUROPE)
                .setHttpTimeout(5000L)
                .build())
        ).forEach((node) -> node.on(TrackStartEvent.class).subscribe((event) -> {
            System.out.printf("%s: track started: %s%n", event.getNode().getName(), event.getTrack().getInfo());
        }));
    }

    private void registerLavalinkListeners() {
        this.client.on(ReadyEvent.class).subscribe((event) ->
                System.out.printf("Node '%s' is ready, session id is '%s'!%n", event.getNode().getName(), event.getSessionId()));

        this.client.on(StatsEvent.class).subscribe((event) ->
                System.out.printf("Node '%s' has stats, current players: %d/%d%n", event.getNode().getName(), event.getPlayingPlayers(), event.getPlayers()));
    }

    public static void main(String[] args) throws InterruptedException {
        getInstance().start();
    }

    public synchronized void start() throws InterruptedException {
        if (running) {
            System.out.println("Bot is already running.");
            return;
        }

        try {
            this.jda = bot.build();
            this.jda.awaitReady();
            this.running = true;
            this.startTime = Instant.now();
            System.out.println("Bot started successfully.");
        } catch (Exception e) {
            this.running = false;
            throw e;
        }
    }

    public synchronized void shutdown() {
        if (!running) {
            System.out.println("Bot is not running.");
            return;
        }

        if (jda != null) {
            jda.shutdownNow();
            jda = null;
        }
        this.running = false;
        this.startTime = null;
        System.out.println("Bot has been shut down.");
    }

    public boolean isRunning() {
        return running;
    }

    public JDA getJda() {
        return jda;
    }

    public Color getDefaultColor() {
        return defaultColor;
    }

    public Color getErrorColor() {
        return errorColor;
    }

    public ChatGPT getGpt() {
        return gpt;
    }

    public static EnumSet<GatewayIntent> createIntents() {
        return EnumSet.of(
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_PRESENCES
        );
    }

    public HashMap<String, GuildSettings> getGuildSettingsHashMap() {
        return guildSettingsHashMap;
    }

    public void setGuildSettingsHashMap(HashMap<String, GuildSettings> guildSettingsHashMap) {
        this.guildSettingsHashMap = guildSettingsHashMap;
    }
}
