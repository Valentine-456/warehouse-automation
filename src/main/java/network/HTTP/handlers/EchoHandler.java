package network.HTTP.handlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class EchoHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        StringBuilder builder = new StringBuilder();

        builder.append("<h1>URI: ").append(exchange.getRequestURI()).append("</h1>");

        Headers headers = exchange.getRequestHeaders();
        for (String header : headers.keySet()) {
            builder.append("<p>").append(header).append("=")
                    .append(headers.getFirst(header)).append("</p>");
        }

        byte[] bytes = builder.toString().getBytes();
        exchange.sendResponseHeaders(200, bytes.length);

        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}
