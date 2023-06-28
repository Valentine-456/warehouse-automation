package network.HTTP.handlers;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dbService.Product;
import dbService.ProductSQLiteService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

public class APIHandler implements HttpHandler {
    private final String DBConnectionURI = "storeHTTPServer.db";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            if (Objects.equals(method, "GET") && path.startsWith("/api/good/")) {
                this.handleGetProductByName(exchange);
            } else if (Objects.equals(method, "POST") && path.startsWith("/api/good/")) {
                this.handleUpdateProductByName(exchange);
            } else if (Objects.equals(method, "DELETE") && path.startsWith("/api/good/")) {
                this.handleDeleteProductByName(exchange);
            } else if (Objects.equals(method, "PUT") && Objects.equals(path, "/api/good")) {
                this.handleCreateProduct(exchange);
            } else {
                this.handleNotFound(exchange);
            }
        } catch (Exception e) {
            this.handleInternalServerError(exchange, e);
        }
    }

    private void handleGetProductByName(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] pathSplit = path.split("/");
        if (pathSplit.length != 4) {
            this.handleBadRequest(exchange);
            return;
        }
        String productName = pathSplit[3];
        ProductSQLiteService productService = new ProductSQLiteService(this.DBConnectionURI);
        Product product = (Product) productService.findOne(productName);
        if (product == null) {
            this.handleNotFound(exchange);
        } else {
            byte[] responseBytes = JsonWriter.objectToJson(product).getBytes(StandardCharsets.UTF_8);
            OutputStream os = exchange.getResponseBody();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, responseBytes.length);
            os.write(responseBytes);
            os.flush();
            os.close();
        }
    }

    private void handleCreateProduct(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        String requestBody = new String(is.readAllBytes());
        Map<String, Object> requestJsonMap = JsonReader.jsonToMaps(requestBody);

        String name = (String) requestJsonMap.get("name");
        Long quantity = (Long) requestJsonMap.get("quantity");
        String priceStr = (String) requestJsonMap.get("price");
        String description = (String) requestJsonMap.get("description");
        String manufacturer = (String) requestJsonMap.get("manufacturer");

        boolean nameIsEmpty = name == null || name.isBlank();
        boolean quantityIsEmpty = quantity == null || quantity < 0;
        boolean priceIsEmpty = priceStr == null || priceStr.isBlank();
        if (nameIsEmpty || priceIsEmpty || quantityIsEmpty) {
            this.handleBadRequest(exchange);
            return;
        }

        int quantityInt = Math.toIntExact(quantity);
        BigDecimal price = BigDecimal.valueOf(Double.parseDouble(priceStr));
        ProductSQLiteService productService = new ProductSQLiteService(this.DBConnectionURI);
        Product product = new Product(name);
        product.setQuantity(quantityInt);
        product.setPrice(price);
        product.description = description;
        product.manufacturer = manufacturer;
        productService.create(product);
        Product createdProduct = (Product) productService.findOne(name);

        OutputStream os = exchange.getResponseBody();
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        if (createdProduct == null) {
            exchange.sendResponseHeaders(409, 0);
            os.close();
            return;
        }
        byte[] responseBytes = JsonWriter.objectToJson(createdProduct).getBytes();
        exchange.sendResponseHeaders(201, responseBytes.length);
        os.write(responseBytes);
        os.flush();
        os.close();
    }

    private void handleUpdateProductByName(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] pathSplit = path.split("/");
        if (pathSplit.length != 4) {
            this.handleBadRequest(exchange);
            return;
        }
        String productName = pathSplit[3];

        InputStream is = exchange.getRequestBody();
        String requestBody = new String(is.readAllBytes());
        Map<String, Object> requestJsonMap = JsonReader.jsonToMaps(requestBody);

        Long quantity = (Long) requestJsonMap.get("quantity");
        String priceStr = (String) requestJsonMap.get("price");
        String description = (String) requestJsonMap.get("description");
        String manufacturer = (String) requestJsonMap.get("manufacturer");

        boolean quantityIsEmpty = quantity == null || quantity < 0;
        boolean priceIsEmpty = priceStr == null || priceStr.isBlank();
        if (priceIsEmpty || quantityIsEmpty) {
            this.handleBadRequest(exchange);
            return;
        }

        int quantityInt = Math.toIntExact(quantity);
        BigDecimal price = BigDecimal.valueOf(Double.parseDouble(priceStr));
        ProductSQLiteService productService = new ProductSQLiteService(this.DBConnectionURI);
        Product product = (Product) productService.findOne(productName);
        if (product == null) {
            this.handleNotFound(exchange);
            return;
        }

        product.setQuantity(quantityInt);
        product.setPrice(price);
        product.description = description == null ? product.description : description;
        product.manufacturer = manufacturer == null ? product.manufacturer : manufacturer;
        productService.update(product);

        OutputStream os = exchange.getResponseBody();
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(204, 0);
        os.close();
    }

    private void handleDeleteProductByName(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] pathSplit = path.split("/");
        if (pathSplit.length != 4) {
            this.handleBadRequest(exchange);
            return;
        }
        String productName = pathSplit[3];

        ProductSQLiteService productService = new ProductSQLiteService(this.DBConnectionURI);
        Product product = (Product) productService.findOne(productName);
        if (product == null) {
            this.handleNotFound(exchange);
            return;
        }
        productService.delete(product);
        OutputStream os = exchange.getResponseBody();
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(204, 0);
        os.close();
    }

    private void handleNotFound(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(404, 0);
        OutputStream os = exchange.getResponseBody();
        os.close();
    }

    private void handleBadRequest(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(400, 0);
        OutputStream os = exchange.getResponseBody();
        os.close();
    }

    private void handleInternalServerError(HttpExchange exchange, Exception e) throws IOException {
        e.printStackTrace();
        exchange.sendResponseHeaders(500, e.getMessage().getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(e.getMessage().getBytes());
        os.flush();
        os.close();
    }
}
