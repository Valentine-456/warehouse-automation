package dbService;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProductSQLiteServiceTests {
    @BeforeClass
    public static void clearTestSpace() throws SQLException {
        cleanTestDatabase();
        DBService catService = new CategorySQLiteService("products.test.db");
        if(catService.read().size() == 0)
            catService.create(new Category("Fruits"));
    }

    public static void cleanTestDatabase() {
        File db = new File("products.test.db");
        if (db.exists()) db.delete();
    }

    @After
    public void clean() {
        cleanTestDatabase();
    }

    @Test
    public void establishConnectionWithDB_NoException() throws SQLException {
        DBService service = new ProductSQLiteService("products.test.db");
        System.out.println(service.read().size());
    }

    @Test
    public void insertValues_AllValuesAreInsertedCorrectly() throws SQLException {
        DBService service = new ProductSQLiteService("products.test.db");
        DBService catService = new CategorySQLiteService("products.test.db");
        Category cat = (Category) catService.read().get(0);

        Product product1 = new Product("Apples", cat.name);
        product1.description = "Delicious";
        product1.setQuantity(20);
        product1.setPrice(BigDecimal.valueOf(5.25));
        service.create(product1);

        product1.name = "Bananas";
        product1.setPrice(BigDecimal.valueOf(3.6));
        service.create(product1);
        Assert.assertEquals(service.read().size(), 2);
        clean();
    }

    @Test
    public void updateValues_ValuesActuallyChanged() throws SQLException {
        DBService service = new ProductSQLiteService("products.test.db");
        DBService catService = new CategorySQLiteService("products.test.db");
        Category cat = (Category) catService.read().get(0);

        Product product1 = new Product("Mushrooms", cat.name);
        product1.description = "Delicious";
        product1.setQuantity(20);
        product1.setPrice(BigDecimal.valueOf(5.25));
        service.create(product1);

        product1.description = "Probably poisonous";
        product1.setQuantity(3);
        product1.setPrice(BigDecimal.valueOf(49.99));
        service.update(product1);

        Product productUpdated = (Product) service.findOne("Mushrooms");
        Assert.assertEquals(productUpdated.description, "Probably poisonous");
        Assert.assertEquals(productUpdated.getQuantity(), 3);
        clean();
    }

    @Test
    public void deleteValues_DeletedValuesAreNoLongerInDatabase() throws SQLException {
        DBService service = new ProductSQLiteService("products.test.db");
        DBService catService = new CategorySQLiteService("products.test.db");
        Category cat = (Category) catService.read().get(0);

        Product product1 = new Product("Apples", cat.name);
        product1.description = "Delicious";
        product1.setQuantity(20);
        product1.setPrice(BigDecimal.valueOf(5.25));
        service.create(product1);
        service.delete(product1);

        Assert.assertEquals(service.read().size(), 0);
        clean();
    }

    @Test
    public void listByCriteria_ReadsOnlyCorrectItems() throws SQLException {
        DBService service = new ProductSQLiteService("products.test.db");
        DBService catService = new CategorySQLiteService("products.test.db");
        Category cat = (Category) catService.read().get(0);

        Product product1 = new Product("0", cat.name);
        product1.description = "Delicious";
        product1.setQuantity(1);
        product1.setPrice(BigDecimal.valueOf(5.25));
        service.create(product1);
        for (int i = 2; i < 20; i++) {
            product1.name = String.valueOf(i);
            product1.setQuantity(i * 10);
            service.create(product1);
        }
        ArrayList<Product> filteredProducts = service.listByCriteria(" quantity >= 100 ");

        Assert.assertEquals(filteredProducts.size(), 10);
        clean();
    }
}
