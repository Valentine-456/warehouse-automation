package network.UDP;

import controller.Command;
import dbService.StorePOJO;

public class StoreUDPClientTests {
    public static void main(String[] args) throws InterruptedException {
        Thread[] clients = new Thread[100];
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            Thread myThread = new Thread(() -> {
                StoreUDPClient client = new StoreUDPClient((byte) (finalI % 2), finalI);
                for (int j = 0; j < 100; j++) {
                    Command command = Command.GET_INVENTORY_QUANTITY;
                    StorePOJO storePOJO = new StorePOJO("Plums", null, 0, null);
                    client.sendMessage(storePOJO, command);
                    client.bPktId++;
                }
            });
            clients[i] = myThread;

        }
        for (Thread client : clients) {
            client.start();
        }
        for (Thread client : clients) {
            client.join();
        }
    }
}