package network.HTTP.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dbService.Category;
import dbService.CategorySQLiteService;
import dbService.ProductSQLiteService;
import network.HTTP.HandleCommonHTTP;
import network.HTTP.UtilHTTP;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

public class StatisticsAPIHandler implements HttpHandler {
    private final String DBConnectionURI = "storeHTTPServer.db";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            if (Objects.equals(method, "GET") && Objects.equals(path, "/api/statistics/totalPriceOfStore")) {
                this.handleGetTotalProductsPrice(exchange);
            } else {
                HandleCommonHTTP.handleNotFound(exchange);
            }
        } catch (Exception e) {
            HandleCommonHTTP.handleInternalServerError(exchange, e);
        }
    }

    private void handleGetTotalProductsPrice(HttpExchange exchange) throws IOException, SQLException {
        String queryString = exchange.getRequestURI().getRawQuery();
        Map<String, String> queryParams = UtilHTTP.queryToMap(queryString);
        String categoryParam = queryParams.get("category");

        String totalPrice = "0.0";
        ProductSQLiteService productService = new ProductSQLiteService(this.DBConnectionURI);
        CategorySQLiteService categoryService = new CategorySQLiteService(this.DBConnectionURI);

        if (categoryParam == null || categoryParam.equals("")) {
            totalPrice = productService.getTotalProductsPrice("");
        } else {
            Category category = (Category) categoryService.findOne(categoryParam);
            if (category == null) {
                HandleCommonHTTP.handleNotFound(exchange);
                return;
            }
            totalPrice = productService.getTotalProductsPrice(category.name);
        }

        byte[] bytes = totalPrice.getBytes();
        OutputStream os = exchange.getResponseBody();
        exchange.sendResponseHeaders(200, bytes.length);
        os.write(bytes);
        os.close();
    }
}
