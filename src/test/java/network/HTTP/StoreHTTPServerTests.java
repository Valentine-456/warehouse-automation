package network.HTTP;

public class StoreHTTPServerTests {
    public static void main(String[] args) {
        StoreHTTPServer server = new StoreHTTPServer(8080);
        server.listen();
    }
}
