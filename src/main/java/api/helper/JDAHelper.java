package api.helper;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import java.util.List;


public class JDAHelper {
    private static JDA jda = null;

    public static void setJDA(JDA jdaInstance) {
        jda = jdaInstance;
    }

    public static JDA getJDA() {
        if (jda == null) {
            throw new IllegalStateException("JDA has not been initialized.");
        }
        return jda;
    }

    public static String[] getServerListName() {
        JDA jda = getJDA();
        List<Guild> guilds = jda.getGuilds();
        String[] guildNames = new String[guilds.size()];
        for (int i = 0; i < guilds.size(); i++) {
            guildNames[i] = guilds.get(i).getName();
        }

        return guildNames;
    }

    public static String[] getServerListIDS() {
        return new String[]{""};
    }
}