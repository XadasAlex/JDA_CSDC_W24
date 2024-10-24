package api.handlers;

import api.HttpRequestHandler;
import com.sun.net.httpserver.HttpExchange;
import net.dv8tion.jda.api.JDA;

import java.io.IOException;

public class StatusHandler extends HttpRequestHandler {
    private final JDA bot;
    public StatusHandler(JDA bot) {
        super(bot);
        this.bot = bot;
    }

    @Override
    protected String handleRequest(HttpExchange exchange) throws IOException {
        return "Bot Status: " + (bot != null ? bot.getStatus() : "Not Running");
    }
}