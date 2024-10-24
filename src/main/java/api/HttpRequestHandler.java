package api;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import net.dv8tion.jda.api.JDA;
import java.io.IOException;
import java.io.OutputStream;

public abstract class HttpRequestHandler implements HttpHandler {
    private final JDA bot;

    public HttpRequestHandler(JDA bot) {
        this.bot = bot;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = handleRequest(exchange);
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    protected abstract String handleRequest(HttpExchange exchange) throws IOException;
}