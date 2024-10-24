package api.handlers;

import api.HttpRequestHandler;
import com.sun.net.httpserver.HttpExchange;
import net.dv8tion.jda.api.JDA;

import java.io.IOException;

public class GuildHandler extends HttpRequestHandler {
    private final JDA bot;

    public enum GuildArgs {
        ALL, TEST, PENIS;
    }

    private final GuildArgs TYPE;

    public GuildHandler(JDA bot, GuildArgs guildArgs) {
        super(bot);
        this.bot = bot;
        this.TYPE = guildArgs;
    }

    @Override
    protected String handleRequest(HttpExchange exchange) throws IOException {
        switch (this.TYPE) {
            case GuildArgs.ALL -> {
                return bot.getGuilds().toString();
            }
            case GuildArgs.TEST -> {
                return bot.getGuilds().getFirst().getMembers().toString();
            }
            case GuildArgs.PENIS -> {
                return "";
            }
        }

        return "Failed";
    }
}
