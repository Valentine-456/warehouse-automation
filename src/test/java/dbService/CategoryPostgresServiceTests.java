package dbService;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class CategoryPostgresServiceTests {
    private static final String conURL = "jdbc:postgresql://localhost:49154/postgres?user=postgres&password=postgrespw";

    @BeforeClass
    @AfterClass
    public static void clearTestSpace() {
        cleanTestDatabase();
    }

    public static void cleanTestDatabase() {
        try {
            Class.forName("org.postgresql.Driver");

            Connection con = DriverManager.getConnection(conURL);
            PreparedStatement st = con.prepareStatement("DELETE from Category WHERE 1 = 1;");
            int result = st.executeUpdate();
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
        CategoryPostgresService service = new CategoryPostgresService(conURL);
        System.out.println(service.read().size());
        service.closeConnection();
    }

    @Test
    public void insertValues_AllValuesAreInsertedCorrectly() throws SQLException {
        CategoryPostgresService service = new CategoryPostgresService(conURL);
        int size = service.read().size();

        Category category = new Category("Fruits");
        category.description = "Ripe";
        service.create(category);

        category.name = "Vegetables";
        service.create(category);
        Assert.assertEquals(service.read().size() - size, 2);
        service.closeConnection();
    }

    @Test
    public void updateValues_ValuesActuallyChanged() throws SQLException {
        CategoryPostgresService service = new CategoryPostgresService(conURL);

        Category category = new Category("Dairy");
        category.description = "";
        service.create(category);

        category.description = "Probably poisonous";
        service.update(category);

        Category category1 = (Category) service.findOne("Dairy");
        Assert.assertEquals(category1.description, "Probably poisonous");
        service.closeConnection();
    }

    @Test
    public void deleteValues_DeletedValuesAreNoLongerInDatabase() throws SQLException {
        CategoryPostgresService service = new CategoryPostgresService(conURL);
        int size = service.read().size();

        Category category = new Category("Fish");
        category.description = "Fresh";
        service.create(category);
        service.delete(category);

        Assert.assertEquals(service.read().size() - size, 0);
        service.closeConnection();
    }

    @Test
    public void listByCriteria_ReadsOnlyCorrectItems() throws SQLException {
        CategoryPostgresService service = new CategoryPostgresService(conURL);

        Category category = new Category("HouseHold");
        service.create(category);

        for (int i = 2; i < 20; i++) {
            category.name = String.valueOf(i);
            service.create(category);
        }
        ArrayList<Product> filteredProducts = service.listByCriteria(" name like '1%' ");

        Assert.assertEquals(filteredProducts.size(), 10);
        service.closeConnection();
    }

}
