package network.HTTP;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

public class HandleCommonHTTP {

    public static void handleNotFound(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(404, 0);
        OutputStream os = exchange.getResponseBody();
        os.close();
    }

    public static void handleBadRequest(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(400, 0);
        OutputStream os = exchange.getResponseBody();
        os.close();
    }

    public static void handleConflict(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(409, 0);
        OutputStream os = exchange.getResponseBody();
        os.close();
    }

    public static void handleInternalServerError(HttpExchange exchange, Exception e) throws IOException {
        e.printStackTrace();
        exchange.sendResponseHeaders(500, e.getMessage().getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(e.getMessage().getBytes());
        os.flush();
        os.close();
    }
}
