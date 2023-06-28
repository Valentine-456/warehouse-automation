package network.HTTP.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import network.HTTP.JWTService;
import network.HTTP.UtilHTTP;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Objects;

public class LoginHandler implements HttpHandler {
    private final String systemLogin = "root";
    private final String systemPassword = "root";
    private JWTService jwtService = new JWTService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        OutputStream os = exchange.getResponseBody();

        if (!Objects.equals(path, "/login") || !Objects.equals(method, "POST")) {
            exchange.sendResponseHeaders(404, 0);
            os.close();
            return;
        }
        String queryString = exchange.getRequestURI().getRawQuery();
        Map<String, String> queryParams = UtilHTTP.queryToMap(queryString);
        String loginParam = queryParams.get("login");
        String passwordParam = queryParams.get("password");

        boolean isLoggedIn = this.loginUser(loginParam, passwordParam);
        if (!isLoggedIn) {
            exchange.sendResponseHeaders(401, 0);
            os.close();
            return;
        }

        String jwtToken = this.jwtService.sign(this.systemLogin);
        byte[] bytes = "OK!".getBytes();
        exchange.getResponseHeaders().add("Authorization", "Bearer " + jwtToken);

        exchange.sendResponseHeaders(200, bytes.length);
        os.write(bytes);
        os.close();
    }

    public boolean loginUser(String login, String password) {
        return Objects.equals(login, this.systemLogin) &&
                Objects.equals(password, this.systemPassword);
    }
}
