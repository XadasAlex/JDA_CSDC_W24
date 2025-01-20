package listeners;


import com.google.gson.Gson;
import launcher.Bot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.openjfx.MyApp;
import utils.GuildSettings;
import utils.Helper;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ReadyListener extends ListenerAdapter {
    @Override
    public void onReady(ReadyEvent event) {
        loadGuildSettings();
    }

    private static void loadGuildSettings() {
        Bot bot = Bot.getInstance();
        JDA jda = bot.getJda();
        HashMap<String, GuildSettings> guildSettings = GuildSettings.loadMultiple(jda.getGuilds().stream().map(Guild::getId).toList());
        bot.setGuildSettingsHashMap(guildSettings);
    }
}
