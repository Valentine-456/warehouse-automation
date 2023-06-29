package dbService;

import java.sql.*;
import java.util.ArrayList;

public class ProductSQLiteService implements DBService {
    private Connection con;

    private void initTable() {
        try {
            Statement st0 = con.createStatement();
            st0.executeUpdate("PRAGMA foreign_keys = ON;");

            PreparedStatement st = con.prepareStatement("create table if not exists 'Product' (" +
                    "'name' TEXT PRIMARY KEY,\n" +
                    "'description' TEXT,\n" +
                    "'manufacturer' TEXT,\n" +
                    "'quantity' INTEGER NOT NULL,\n" +
                    "'price' DECIMAL(10, 2) NOT NULL,\n" +
                    "'category_name' TEXT NOT NULL,\n" +
                    "FOREIGN KEY (category_name) REFERENCES Category (name)\n" +
                    "ON UPDATE CASCADE\n " +
                    "ON DELETE CASCADE " +
                    ");");
            int result = st.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит");
            e.printStackTrace();
        }
    }

    public ProductSQLiteService(Connection con) {
        this.con = con;
        this.initTable();
    }

    public ProductSQLiteService(String name) {
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:" + name);
            this.initTable();
        } catch (ClassNotFoundException e) {
            System.out.println("Не знайшли драйвер JDBC");
            e.printStackTrace();
            System.exit(0);
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит");
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Product> read() throws SQLException {
        ArrayList<Product> rows = new ArrayList<>();
        try {
            Statement st = con.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM Product");
            while (res.next()) {
                Product product = new Product(
                        res.getString("name"),
                        res.getString("category_name"));
                product.description = res.getString("description");
                product.manufacturer = res.getString("manufacturer");
                product.setQuantity(res.getInt("quantity"));
                product.setPrice(res.getBigDecimal("price"));
                rows.add(product);
            }
            res.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вибірку даних");
            e.printStackTrace();
            throw e;
        }
        return rows;
    }

    @Override
    public void create(Object obj) throws SQLException {
        Product product = (Product) obj;
        try {
            PreparedStatement statement = con.prepareStatement(
                    "INSERT INTO Product (name, description, manufacturer, quantity, price, category_name)\n" +
                            "VALUES (?, ?, ?, ?, ?, ?);"
            );
            statement.setString(1, product.name);
            statement.setString(2, product.description);
            statement.setString(3, product.manufacturer);
            statement.setInt(4, product.getQuantity());
            statement.setBigDecimal(5, product.getPrice());
            statement.setString(6, product.category_name);

            int result = statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вставку");
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void update(Object obj) throws SQLException {
        Product product = (Product) obj;
        try {
            PreparedStatement statement = con.prepareStatement(
                    "UPDATE Product SET " +
                            "description = ?, " +
                            "manufacturer = ?, " +
                            "quantity = ?, " +
                            "price = ?, " +
                            "category_name = ? " +
                            "WHERE name = ? ;"
            );
            statement.setString(1, product.description);
            statement.setString(2, product.manufacturer);
            statement.setInt(3, product.getQuantity());
            statement.setBigDecimal(4, product.getPrice());
            statement.setString(5, product.category_name);
            statement.setString(6, product.name);

            int result = statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вставку");
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void delete(Object obj) throws SQLException {
        Product product = (Product) obj;
        try {
            PreparedStatement statement = con.prepareStatement(
                    "DELETE FROM Product WHERE name = ? ;");
            statement.setString(1, product.name);
            int result = statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вставку");
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public Object findOne(String primaryKeyValue) throws SQLException {
        ArrayList<Product> rows = new ArrayList<>();
        try {
            PreparedStatement statement = con.prepareStatement(
                    "SELECT * FROM Product WHERE name = ? ;"
            );
            statement.setString(1, primaryKeyValue);
            ResultSet res = statement.executeQuery();

            while (res.next()) {
                Product product = new Product(
                        res.getString("name"),
                        res.getString("category_name")
                );
                product.description = res.getString("description");
                product.manufacturer = res.getString("manufacturer");
                product.setQuantity(res.getInt("quantity"));
                product.setPrice(res.getBigDecimal("price"));
                rows.add(product);
            }
            res.close();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вибірку даних");
            e.printStackTrace();
            throw e;
        }
        if (rows.size() > 0) return rows.get(0);
        else return null;
    }

    @Override
    public ArrayList listByCriteria(String filter) throws SQLException {
        ArrayList<Product> rows = new ArrayList<>();
        try {
            Statement st = con.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM Product WHERE " + filter + " ;");
            while (res.next()) {
                Product product = new Product(
                        res.getString("name"),
                        res.getString("category_name")
                );
                product.description = res.getString("description");
                product.manufacturer = res.getString("manufacturer");
                product.setQuantity(res.getInt("quantity"));
                product.setPrice(res.getBigDecimal("price"));
                rows.add(product);
            }
            res.close();
            st.close();
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вибірку даних");
            e.printStackTrace();
            throw e;
        }
        return rows;
    }
}
