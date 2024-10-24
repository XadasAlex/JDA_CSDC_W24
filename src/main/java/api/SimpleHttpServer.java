package api;

import api.handlers.GuildHandler;
import api.handlers.StatusHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import net.dv8tion.jda.api.JDA;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleHttpServer {
    private final JDA bot;

    public SimpleHttpServer(JDA jda) {
        this.bot = jda;
    }

    private JDA getBot() {
        return this.bot;
    }

    public void start() throws IOException{
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        ExecutorService executor = Executors.newFixedThreadPool(10);
        server.setExecutor(executor);

        this.createServerContext(server);

        server.start();

        System.out.println("Server started on port 8080");
    }

    public void createServerContext(HttpServer server) {
        server.createContext("/status", new StatusHandler(this.bot));
        server.createContext("/guild-all", new GuildHandler(this.bot, GuildHandler.GuildArgs.ALL));
        server.createContext("/guild-test", new GuildHandler(this.bot, GuildHandler.GuildArgs.TEST));
    }


}
