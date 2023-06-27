package dbService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBSQLServiceFacade {
    DBService categoryService;
    DBService productService;

    public DBSQLServiceFacade(String DBfile) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection con = DriverManager.getConnection("jdbc:sqlite:" + DBfile);
            this.categoryService = new CategorySQLiteService(con);
            this.productService = new ProductSQLiteService(con);

        } catch (ClassNotFoundException e) {
            System.out.println("Не знайшли драйвер JDBC");
            e.printStackTrace();
            System.exit(0);
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит");
            e.printStackTrace();
        }
    }


}
