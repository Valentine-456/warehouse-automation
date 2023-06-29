package dbService;

import controller.Command;
import network.UDP.StoreUDPClient;

import java.math.BigDecimal;
import java.sql.SQLException;

public class StoreUDPServerDBIntegrationTests {
    public static void main(String[] args) throws InterruptedException, SQLException {
        StoreUDPClient client = new StoreUDPClient((byte) 2, 2);

        DBService catService = new CategorySQLiteService("products.test.db");
        Category cat = (Category) catService.read().get(0);

        Product product = new Product("Plums", cat.name);
        product.setPrice(BigDecimal.valueOf(19.9));
        product.setQuantity(180);
        StorePOJO storePOJO = new StorePOJO(
                "Plums",
                null,
                10,
                product
        );
        client.sendMessage(storePOJO, Command.ADD_INVENTORY);

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                client.sendMessage(storePOJO, Command.DEDUCT_INVENTORY);
                client.sendMessage(storePOJO, Command.GET_INVENTORY_QUANTITY);
            }).start();
        }
        Thread.sleep(4000);
    }
}
