package network.HTTP;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import network.HTTP.authentificators.AuthIsLoggedIn;
import network.HTTP.handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class StoreHTTPServer {
    public final int port;
    HttpServer server;

    public StoreHTTPServer(int port) {
        this.port = port;
    }

    public void listen() {
        try {
            HttpServer server = HttpServer.create();
            this.server = server;
            server.bind(new InetSocketAddress(this.port), 0);
            this.addHandlers();
            server.setExecutor(Executors.newFixedThreadPool(20));
            System.out.println("Server is running on port: " + this.port);
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addHandlers() {
        HttpContext loginContext = this.server.createContext("/login", new LoginHandler());
        HttpContext productAPIContext = this.server.createContext("/api/good", new ProductAPIHandler());
        HttpContext categoryAPIContext = this.server.createContext("/api/category", new CategoryAPIHandler());
        HttpContext statisticsAPIContext = this.server.createContext("/api/statistics", new StatisticsAPIHandler());
        HttpContext staticContext = this.server.createContext("/static", new EchoHandler());

        Authenticator authIsLoggedIn = new AuthIsLoggedIn();
        productAPIContext.setAuthenticator(authIsLoggedIn);
        categoryAPIContext.setAuthenticator(authIsLoggedIn);
        statisticsAPIContext.setAuthenticator(authIsLoggedIn);
    }

}
