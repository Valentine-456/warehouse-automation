package dbService;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

public class CategorySQLiteServiceTests {
    @BeforeClass
    public static void clearTestSpace() {
        cleanTestDatabase();
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
    public void establishConnectionWithDB_NoException() {
        DBService service = new CategorySQLiteService("products.test.db");
        System.out.println(service.read().size());
    }

    @Test
    public void insertValues_AllValuesAreInsertedCorrectly() {
        DBService service = new CategorySQLiteService("products.test.db");
        int size = service.read().size();

        Category category = new Category("Fruits");
        category.description = "Ripe";
        service.create(category);

        category.name = "Vegetables";
        service.create(category);
        Assert.assertEquals(service.read().size() - size, 2);
        clean();
    }

    @Test
    public void updateValues_ValuesActuallyChanged() {
        DBService service = new CategorySQLiteService("products.test.db");

        Category category = new Category("Dairy");
        category.description = "";
        service.create(category);

        category.description = "Probably poisonous";
        service.update(category);

        Category category1 = (Category) service.findOne("Dairy");
        Assert.assertEquals(category1.description, "Probably poisonous");
        clean();
    }

    @Test
    public void deleteValues_DeletedValuesAreNoLongerInDatabase() {
        DBService service = new CategorySQLiteService("products.test.db");
        int size = service.read().size();

        Category category = new Category("Fish");
        category.description = "Fresh";
        service.create(category);
        service.delete(category);

        Assert.assertEquals(service.read().size() - size, 0);
        clean();
    }

    @Test
    public void listByCriteria_ReadsOnlyCorrectItems() {
        DBService service = new CategorySQLiteService("products.test.db");

        Category category = new Category("HouseHold");
        service.create(category);

        for (int i = 2; i < 20; i++) {
            category.name = String.valueOf(i);
            service.create(category);
        }
        ArrayList<Product> filteredProducts = service.listByCriteria(" name like '1%' ");

        Assert.assertEquals(filteredProducts.size(), 10);
        clean();
    }
}
