package network.HTTP;

public class StoreHTTPMultipleClients {
    public static void main(String[] args) throws InterruptedException {
        Long start = System.currentTimeMillis();
        Thread[] clients = new Thread[100];
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            Thread oneClient = new Thread(() -> {
                for (int j = 0; j < 5; j++) {
                    HttpClient.sendGetHTTPRequest("http://127.0.0.1:8080/api/good");
                    HttpClient.sendGetHTTPRequest("http://127.0.0.1:8080/api/category");
                    HttpClient.sendGetHTTPRequest("http://127.0.0.1:8080/api/good/Mango");
                    HttpClient.sendGetHTTPRequest("http://127.0.0.1:8080/api/category/Fruits");
                }
            });
            clients[i] = oneClient;
        }

        for (Thread client : clients) {
            client.start();
        }
        for (Thread client : clients) {
            client.join();
        }
        Long end = System.currentTimeMillis();
        System.out.println("Total time: " + (end - start));
    }
}
