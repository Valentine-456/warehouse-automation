package network.HTTP;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import network.HTTP.authentificators.AuthIsLoggedIn;
import network.HTTP.handlers.APIHandler;
import network.HTTP.handlers.EchoHandler;
import network.HTTP.handlers.LoginHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

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
            System.out.println("Server is running on port: " + this.port);
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addHandlers() {
        HttpContext loginContext = this.server.createContext("/login", new LoginHandler());
        HttpContext APIContext = this.server.createContext("/api", new APIHandler());
        HttpContext staticContext = this.server.createContext("/static", new EchoHandler());
        APIContext.setAuthenticator(new AuthIsLoggedIn());
    }

}
