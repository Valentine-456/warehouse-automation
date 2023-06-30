package network.HTTP;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class UtilHTTP {
    public static Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        if (query == null) return result;

        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                String key = decodeURLComponent(entry[0]);
                String value = decodeURLComponent(entry[1]);
                result.put(key, value);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
    }

    private static String decodeURLComponent(String component) {
        return URLDecoder.decode(component, StandardCharsets.UTF_8);
    }

    public static void enableCORS(HttpExchange httpExchange) throws IOException {
        httpExchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        httpExchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
        httpExchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        httpExchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
    }
}
