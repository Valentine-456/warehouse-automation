package network.HTTP.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ServeStaticHandler implements HttpHandler {
    private final String rootDirectory;
    private final Map<String, String> mimeTypes;

    public ServeStaticHandler(String rootDirectory) {
        this.rootDirectory = rootDirectory;
        this.mimeTypes = createMimeTypesMap();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();
        String filePath = rootDirectory + requestPath;

        File file = new File(filePath);
        System.out.println(file.getPath());
        if (file.exists() && file.isFile()) {
            String mimeType = getMimeType(filePath);

            exchange.getResponseHeaders().set("Content-Type", mimeType);

            Path path = Paths.get(filePath);
            byte[] fileData = Files.readAllBytes(path);

            exchange.sendResponseHeaders(200, fileData.length);
            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(fileData);
            outputStream.close();
        } else {
            exchange.sendResponseHeaders(404, 0);
            exchange.close();
        }
    }

    private String getMimeType(String filePath) {
        String extension = filePath.substring(filePath.lastIndexOf('.') + 1);
        return mimeTypes.getOrDefault(extension, "application/octet-stream");
    }

    private Map<String, String> createMimeTypesMap() {
        Map<String, String> mimeTypes = new HashMap<>();
        mimeTypes.put("txt", "text/plain");
        mimeTypes.put("html", "text/html");
        mimeTypes.put("css", "text/css");
        mimeTypes.put("js", "text/javascript");
        mimeTypes.put("json", "application/json");
        mimeTypes.put("jpg", "image/jpeg");
        mimeTypes.put("png", "image/png");
        mimeTypes.put("gif", "image/gif");
        mimeTypes.put("ts", "text/typescript");
        mimeTypes.put("vue", "text/plain");
        return mimeTypes;
    }
}