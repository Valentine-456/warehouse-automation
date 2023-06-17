package network.UDP;

import controller.Decryptor;

public class StoreUDPServerTests {
    public static void main(String[] args) {
        StoreUDPServer server = new StoreUDPServer(10);
        server.listen(4445);
    }
}
