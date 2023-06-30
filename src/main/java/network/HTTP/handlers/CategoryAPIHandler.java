package network.HTTP.handlers;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dbService.Category;
import dbService.CategoryPostgresService;
import network.HTTP.HandleCommonHTTP;
import network.HTTP.UtilHTTP;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class CategoryAPIHandler implements HttpHandler {
    private final String DBConnectionURI = "storeHTTPServer.db";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            UtilHTTP.enableCORS(exchange);
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                exchange.sendResponseHeaders(204, -1);
                exchange.getResponseBody().close();
                return;
            }
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            if (Objects.equals(method, "GET") && path.startsWith("/api/category/")) {
                this.handleGetCategoryByName(exchange);
            } else if (Objects.equals(method, "GET") && Objects.equals(path, "/api/category")) {
                this.handleGetAllCategories(exchange);
            } else if (Objects.equals(method, "POST") && path.startsWith("/api/category/")) {
                this.handleUpdateCategoryByName(exchange);
            } else if (Objects.equals(method, "DELETE") && path.startsWith("/api/category/")) {
                this.handleDeleteCategoryByName(exchange);
            } else if (Objects.equals(method, "PUT") && Objects.equals(path, "/api/category")) {
                this.handleCreateCategory(exchange);
            } else {
                HandleCommonHTTP.handleNotFound(exchange);
            }
        } catch (Exception e) {
            HandleCommonHTTP.handleInternalServerError(exchange, e);
        }
    }

    private void handleGetAllCategories(HttpExchange exchange) throws IOException {
        try {
            CategoryPostgresService categoryService = new CategoryPostgresService(this.DBConnectionURI);
            ArrayList<Category> categories = categoryService.read();
            byte[] responseBytes = JsonWriter.objectToJson(categories).getBytes(StandardCharsets.UTF_8);
            OutputStream os = exchange.getResponseBody();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, responseBytes.length);
            os.write(responseBytes);
            os.flush();
            os.close();
            categoryService.closeConnection();
        } catch (SQLException e) {
            HandleCommonHTTP.handleBadRequest(exchange);
        }
    }

    private void handleGetCategoryByName(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String[] pathSplit = path.split("/");
            if (pathSplit.length != 4) {
                HandleCommonHTTP.handleBadRequest(exchange);
                return;
            }
            String categoryName = pathSplit[3];
            CategoryPostgresService categoryService = new CategoryPostgresService(this.DBConnectionURI);
            Category category = null;

            category = (Category) categoryService.findOne(categoryName);
            if (category == null) {
                HandleCommonHTTP.handleNotFound(exchange);
            } else {
                byte[] responseBytes = JsonWriter.objectToJson(category).getBytes(StandardCharsets.UTF_8);
                OutputStream os = exchange.getResponseBody();
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, responseBytes.length);
                os.write(responseBytes);
                os.flush();
                os.close();
            }
            categoryService.closeConnection();
        } catch (SQLException e) {
            HandleCommonHTTP.handleBadRequest(exchange);
        }
    }

    private void handleCreateCategory(HttpExchange exchange) throws IOException {
        try {
            InputStream is = exchange.getRequestBody();
            String requestBody = new String(is.readAllBytes());
            Map<String, Object> requestJsonMap = JsonReader.jsonToMaps(requestBody);

            String name = (String) requestJsonMap.get("name");
            String description = (String) requestJsonMap.get("description");

            boolean nameIsEmpty = name == null || name.isBlank();
            if (nameIsEmpty) {
                HandleCommonHTTP.handleConflict(exchange);
                return;
            }

            CategoryPostgresService categoryService = new CategoryPostgresService(this.DBConnectionURI);
            Category category = new Category(name);
            category.description = description;
            categoryService.create(category);
            Category createdCategory = (Category) categoryService.findOne(name);

            OutputStream os = exchange.getResponseBody();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            if (createdCategory == null) {
                HandleCommonHTTP.handleConflict(exchange);
                categoryService.closeConnection();
                return;
            }
            byte[] responseBytes = JsonWriter.objectToJson(createdCategory).getBytes();
            exchange.sendResponseHeaders(201, responseBytes.length);
            os.write(responseBytes);
            os.flush();
            os.close();
            categoryService.closeConnection();
        } catch (SQLException e) {
            HandleCommonHTTP.handleBadRequest(exchange);
        }
    }

    private void handleDeleteCategoryByName(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String[] pathSplit = path.split("/");
            if (pathSplit.length != 4) {
                HandleCommonHTTP.handleBadRequest(exchange);
                return;
            }
            String categoryName = pathSplit[3];

            CategoryPostgresService categoryService = new CategoryPostgresService(this.DBConnectionURI);
            Category category = (Category) categoryService.findOne(categoryName);
            if (category == null) {
                HandleCommonHTTP.handleNotFound(exchange);
                categoryService.closeConnection();
                return;
            }
            categoryService.delete(category);
            OutputStream os = exchange.getResponseBody();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(204, 0);
            os.close();
            categoryService.closeConnection();
        } catch (SQLException e) {
            HandleCommonHTTP.handleBadRequest(exchange);
        }
    }

    private void handleUpdateCategoryByName(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String[] pathSplit = path.split("/");
            if (pathSplit.length != 4) {
                HandleCommonHTTP.handleBadRequest(exchange);
                return;
            }
            String categoryName = pathSplit[3];

            InputStream is = exchange.getRequestBody();
            String requestBody = new String(is.readAllBytes());
            Map<String, Object> requestJsonMap = JsonReader.jsonToMaps(requestBody);


            String description = (String) requestJsonMap.get("description");

            CategoryPostgresService categoryService = new CategoryPostgresService(this.DBConnectionURI);
            Category category = (Category) categoryService.findOne(categoryName);
            if (category == null) {
                HandleCommonHTTP.handleNotFound(exchange);
                categoryService.closeConnection();
                return;
            }
            category.description = description == null ? category.description : description;
            categoryService.update(category);

            OutputStream os = exchange.getResponseBody();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(204, 0);
            os.close();
            categoryService.closeConnection();
        } catch (SQLException e) {
            HandleCommonHTTP.handleBadRequest(exchange);
        }
    }
}
