package network.HTTP.handlers;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dbService.Product;
import dbService.ProductSQLiteService;
import network.HTTP.HandleCommonHTTP;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class ProductAPIHandler implements HttpHandler {
    private final String DBConnectionURI = "storeHTTPServer.db";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            if (Objects.equals(method, "GET") && path.startsWith("/api/good/")) {
                this.handleGetProductByName(exchange);
            } else if (Objects.equals(method, "GET") && Objects.equals(path, "/api/good")) {
                this.handleGetAllProducts(exchange);
            } else if (Objects.equals(method, "POST") && path.startsWith("/api/good/")) {
                this.handleUpdateProductByName(exchange);
            } else if (Objects.equals(method, "DELETE") && path.startsWith("/api/good/")) {
                this.handleDeleteProductByName(exchange);
            } else if (Objects.equals(method, "PUT") && Objects.equals(path, "/api/good")) {
                this.handleCreateProduct(exchange);
            } else {
                HandleCommonHTTP.handleNotFound(exchange);
            }
        } catch (Exception e) {
            HandleCommonHTTP.handleInternalServerError(exchange, e);
        }
    }

    private void handleGetAllProducts(HttpExchange exchange) throws IOException {
        try {
            ProductSQLiteService productService = new ProductSQLiteService(this.DBConnectionURI);
            ArrayList<Product> products = productService.read();
            byte[] responseBytes = JsonWriter.objectToJson(products).getBytes(StandardCharsets.UTF_8);
            OutputStream os = exchange.getResponseBody();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, responseBytes.length);
            os.write(responseBytes);
            os.flush();
            os.close();
        } catch (SQLException e) {
            HandleCommonHTTP.handleBadRequest(exchange);
        }
    }

    private void handleGetProductByName(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String[] pathSplit = path.split("/");
            if (pathSplit.length != 4) {
                HandleCommonHTTP.handleBadRequest(exchange);
                return;
            }
            String productName = pathSplit[3];
            ProductSQLiteService productService = new ProductSQLiteService(this.DBConnectionURI);
            Product product = null;

            product = (Product) productService.findOne(productName);
            if (product == null) {
                HandleCommonHTTP.handleNotFound(exchange);
            } else {
                byte[] responseBytes = JsonWriter.objectToJson(product).getBytes(StandardCharsets.UTF_8);
                OutputStream os = exchange.getResponseBody();
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, responseBytes.length);
                os.write(responseBytes);
                os.flush();
                os.close();
            }
        } catch (SQLException e) {
            HandleCommonHTTP.handleBadRequest(exchange);
        }
    }

    private void handleCreateProduct(HttpExchange exchange) throws IOException {
        try {
            InputStream is = exchange.getRequestBody();
            String requestBody = new String(is.readAllBytes());
            Map<String, Object> requestJsonMap = JsonReader.jsonToMaps(requestBody);

            String name = (String) requestJsonMap.get("name");
            String category_name = (String) requestJsonMap.get("category_name");
            Long quantity = (Long) requestJsonMap.get("quantity");
            String priceStr = (String) requestJsonMap.get("price");
            String description = (String) requestJsonMap.get("description");
            String manufacturer = (String) requestJsonMap.get("manufacturer");

            boolean nameIsEmpty = name == null || name.isBlank();
            boolean categoryNameIsEmpty = category_name == null || category_name.isBlank();
            boolean quantityIsEmpty = quantity == null || quantity < 0;
            boolean priceIsEmpty = priceStr == null || priceStr.isBlank();
            if (nameIsEmpty || priceIsEmpty || quantityIsEmpty || categoryNameIsEmpty) {
                HandleCommonHTTP.handleConflict(exchange);
                return;
            }

            int quantityInt = Math.toIntExact(quantity);
            BigDecimal price = BigDecimal.valueOf(Double.parseDouble(priceStr));
            ProductSQLiteService productService = new ProductSQLiteService(this.DBConnectionURI);
            Product product = new Product(name, category_name);
            product.setQuantity(quantityInt);
            product.setPrice(price);
            product.description = description;
            product.manufacturer = manufacturer;
            productService.create(product);
            Product createdProduct = (Product) productService.findOne(name);

            OutputStream os = exchange.getResponseBody();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            if (createdProduct == null) {
                HandleCommonHTTP.handleConflict(exchange);
                return;
            }
            byte[] responseBytes = JsonWriter.objectToJson(createdProduct).getBytes();
            exchange.sendResponseHeaders(201, responseBytes.length);
            os.write(responseBytes);
            os.flush();
            os.close();
        } catch (SQLException e) {
            HandleCommonHTTP.handleBadRequest(exchange);
        }
    }

    private void handleUpdateProductByName(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String[] pathSplit = path.split("/");
            if (pathSplit.length != 4) {
                HandleCommonHTTP.handleBadRequest(exchange);
                return;
            }
            String productName = pathSplit[3];

            InputStream is = exchange.getRequestBody();
            String requestBody = new String(is.readAllBytes());
            Map<String, Object> requestJsonMap = JsonReader.jsonToMaps(requestBody);

            String category_name = (String) requestJsonMap.get("category_name");
            Long quantity = (Long) requestJsonMap.get("quantity");
            String priceStr = (String) requestJsonMap.get("price");
            String description = (String) requestJsonMap.get("description");
            String manufacturer = (String) requestJsonMap.get("manufacturer");

            boolean quantityIsEmpty = quantity == null || quantity < 0;
            boolean priceIsEmpty = priceStr == null || priceStr.isBlank();
            if (priceIsEmpty || quantityIsEmpty) {
                HandleCommonHTTP.handleConflict(exchange);
                return;
            }

            int quantityInt = Math.toIntExact(quantity);
            BigDecimal price = BigDecimal.valueOf(Double.parseDouble(priceStr));
            ProductSQLiteService productService = new ProductSQLiteService(this.DBConnectionURI);
            Product product = (Product) productService.findOne(productName);
            if (product == null) {
                HandleCommonHTTP.handleNotFound(exchange);
                return;
            }

            product.setQuantity(quantityInt);
            product.setPrice(price);
            product.description = description == null ? product.description : description;
            product.manufacturer = manufacturer == null ? product.manufacturer : manufacturer;
            boolean categoryNameIsEmpty = category_name == null || category_name.isBlank();
            product.category_name = categoryNameIsEmpty ? product.category_name : category_name;
            productService.update(product);

            OutputStream os = exchange.getResponseBody();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(204, 0);
            os.close();
        } catch (SQLException e) {
            HandleCommonHTTP.handleBadRequest(exchange);
        }
    }

    private void handleDeleteProductByName(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String[] pathSplit = path.split("/");
            if (pathSplit.length != 4) {
                HandleCommonHTTP.handleBadRequest(exchange);
                return;
            }
            String productName = pathSplit[3];

            ProductSQLiteService productService = new ProductSQLiteService(this.DBConnectionURI);
            Product product = (Product) productService.findOne(productName);
            if (product == null) {
                HandleCommonHTTP.handleNotFound(exchange);
                return;
            }
            productService.delete(product);
            OutputStream os = exchange.getResponseBody();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(204, 0);
            os.close();
        } catch (SQLException e) {
            HandleCommonHTTP.handleBadRequest(exchange);
        }
    }
}
