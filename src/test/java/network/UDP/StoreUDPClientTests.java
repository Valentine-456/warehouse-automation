package network.UDP;

public class StoreUDPClientTests {
    public static void main(String[] args) throws InterruptedException {
        Thread[] clients = new Thread[10];
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            Thread myThread = new Thread(() -> {
                StoreUDPClient client = new StoreUDPClient((byte) (finalI % 2), finalI);
                client.sendMessage();
            });
            clients[i] = myThread;
            myThread.run();
        }
        for (Thread client: clients) {
            client.join();
        };
    }
}
