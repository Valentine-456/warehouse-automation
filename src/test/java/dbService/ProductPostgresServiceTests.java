package dbService;

import org.junit.*;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProductPostgresServiceTests {

    private static final String conURL = "jdbc:postgresql://localhost:49154/postgres?user=postgres&password=postgrespw";

    @AfterClass
    public static void clearTestSpace() {
        cleanTestDatabase();
    }

    @BeforeClass
    public static void insertTestCategory() throws SQLException {
        cleanTestDatabase();
        CategoryPostgresService catService = new CategoryPostgresService(conURL);
        if(catService.read().size() == 0)
            catService.create(new Category("Fruits"));
        catService.closeConnection();
    }

    public static void cleanTestDatabase() {
        try {
            Class.forName("org.postgresql.Driver");

            Connection con = DriverManager.getConnection(conURL);
            PreparedStatement st = con.prepareStatement("DELETE from Product WHERE 1 = 1;");
            PreparedStatement st2 = con.prepareStatement("DELETE from Category WHERE 1 = 1;");

            st.executeUpdate();
            st2.executeUpdate();
            con.close();
        } catch (ClassNotFoundException e) {
            System.out.println("Не знайшли драйвер JDBC");
            e.printStackTrace();
            System.exit(0);
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит");
            e.printStackTrace();
        }
    }

    @Test
    public void establishConnectionWithDB_NoException() throws SQLException {
        ProductPostgresService service = new ProductPostgresService(conURL);
        System.out.println(service.read().size());
        service.closeConnection();
    }

    @Test
    public void insertValues_AllValuesAreInsertedCorrectly() throws SQLException {
        ProductPostgresService service = new ProductPostgresService(conURL);
        CategoryPostgresService catService = new CategoryPostgresService(conURL);
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
        catService.closeConnection();
        service.closeConnection();
    }

    @Test
    public void updateValues_ValuesActuallyChanged() throws SQLException {
        ProductPostgresService service = new ProductPostgresService(conURL);
        CategoryPostgresService catService = new CategoryPostgresService(conURL);
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
        service.closeConnection();
        catService.closeConnection();
    }

    @Test
    public void deleteValues_DeletedValuesAreNoLongerInDatabase() throws SQLException {
        ProductPostgresService service = new ProductPostgresService(conURL);
        CategoryPostgresService catService = new CategoryPostgresService(conURL);
        Category cat = (Category) catService.read().get(0);

        Product product1 = new Product("Apples", cat.name);
        product1.description = "Delicious";
        product1.setQuantity(20);
        product1.setPrice(BigDecimal.valueOf(5.25));
        service.create(product1);
        service.delete(product1);


        Assert.assertEquals(service.read().size(), 0);
        service.closeConnection();
        catService.closeConnection();
    }

    @Test
    public void listByCriteria_ReadsOnlyCorrectItems() throws SQLException {
        ProductPostgresService service = new ProductPostgresService(conURL);
        CategoryPostgresService catService = new CategoryPostgresService(conURL);
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
        catService.closeConnection();
        service.closeConnection();
    }
}
