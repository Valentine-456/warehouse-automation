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

    //TODO: add try catch, null checking
    public int GET_INVENTORY_QUANTITY(StorePOJO storePOJO) {
        String productName = storePOJO.productName;
        Product product = (Product) this.productService.findOne(productName);
        if(product != null) {
            return product.getQuantity();
        }
        return 0;
    }

    public void DEDUCT_INVENTORY(StorePOJO storePOJO) {
        String productName = storePOJO.productName;
        Product product = (Product) this.productService.findOne(productName);
        product.setQuantity(product.getQuantity() - storePOJO.number);
        this.productService.update(product);
    }

    public void ADD_INVENTORY(StorePOJO storePOJO) {
        Product product = (Product) storePOJO.pojo;
        this.productService.create(product);
    }

    public void ADD_PRODUCT_GROUP(StorePOJO storePOJO) {
        Category category = (Category) storePOJO.pojo;
        this.categoryService.create(category);
    }

    public void ADD_PRODUCT_NAME_TO_GROUP(StorePOJO storePOJO) {
        return;
    }

    public void SET_PRODUCT_PRICE(StorePOJO storePOJO) {
        Product product = (Product) storePOJO.pojo;
        this.productService.update(product);
    }

}
