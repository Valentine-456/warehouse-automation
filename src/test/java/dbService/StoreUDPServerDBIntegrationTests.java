package dbService;

import network.UDP.StoreUDPClient;

public class StoreUDPServerDBIntegrationTests {
    public static void main(String[] args) {
        StoreUDPClient client = new StoreUDPClient((byte) 2, 2);
        client.sendMessage();
    }
}
