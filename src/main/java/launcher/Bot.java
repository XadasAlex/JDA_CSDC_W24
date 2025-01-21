package launcher;

import api.ChatGPT;

import commands.battleship.*;
import commands.chat.stats.CmdAllowStats;
import commands.chat.stats.CmdLeaderBoard;
import commands.chat.stats.CmdStats;
import commands.guild.CmdAnnoy;
import commands.guild.CmdGuildInfo;
import commands.guild.CmdSettingsChatRestricted;
import listeners.*;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import utils.GuildSettings;

import commands.chat.*;
import commands.guild.CmdKick;
import commands.testing.ButtonTest;
import commands.testing.DropDownTest;
import commands.testing.EntityDDTest;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Bot {
    private JDABuilder bot;
    private JDA jda;
    private ChatGPT gpt;

    private final String avatarUrl = "https://example.com/default-avatar.png"; // Placeholder
    private final Color defaultColor = new Color(115, 169, 186);
    private final Color errorColor = new Color(255, 51, 51);

    private boolean running = false; // Neuer Status-Tracker
    private HashMap<String, GuildSettings> guildSettingsHashMap;

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
                //new CmdEmbed(),
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
                new CmdSettingsChatRestricted()
        );

        // JDABuilder vorbereiten, aber nicht starten
        String TOKEN = System.getenv("DISCORD_BOT_TOKEN");
        this.bot = JDABuilder
                .createDefault(TOKEN)
                .enableIntents(INTENTS)
                .enableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE, CacheFlag.ONLINE_STATUS)
                .addEventListeners(
                        new ChatListener(),
                        new StatListener(),
                        commandManagerListener,
                        new ReadyListener(),
                        new JoinLeaveListener(),
                        new SpamListener()
                );
    }

    public synchronized void start() {
        if (running) {
            System.out.println("Bot is already running.");
            return;
        }

        try {
            this.jda = bot.build();
            this.jda.awaitReady();
            this.running = true;
            System.out.println("Bot started successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            this.running = false;
        }
    }

    public synchronized void shutdown() {
        if (!running) {
            System.out.println("Bot is not running.");
            return;
        }

        if (jda != null) {
            jda.shutdown();
            jda = null;
        }
        this.running = false;
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
