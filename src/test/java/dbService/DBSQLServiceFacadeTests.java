package dbService;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

public class DBSQLServiceFacadeTests {
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
        DBSQLServiceFacade dbFacade = new DBSQLServiceFacade("products.test.db");
        System.out.println(dbFacade.productService.read().size());
        System.out.println(dbFacade.categoryService.read().size());
    }
}
