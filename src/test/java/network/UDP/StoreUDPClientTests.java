package network.UDP;

public class StoreUDPClientTests {
    public static void main(String[] args) throws InterruptedException {
        Thread[] clients = new Thread[100];
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            Thread myThread = new Thread(() -> {
                StoreUDPClient client = new StoreUDPClient((byte) (finalI % 2), finalI);
                for (int j = 0; j < 100; j++) {
                    client.sendMessage();
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