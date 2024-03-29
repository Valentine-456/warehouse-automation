package dbService;

import java.sql.*;
import java.util.ArrayList;

public class CategorySQLiteService implements DBService {
    Connection con;

    private void initTable() {
        try {
            Statement st0 = con.createStatement();
            st0.executeUpdate("PRAGMA foreign_keys = ON;");

            PreparedStatement st = con.prepareStatement("create table if not exists 'Category' (" +
                    "'name' TEXT PRIMARY KEY,\n" +
                    "'description' TEXT\n" +
                    ");");
            int result = st.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит");
            e.printStackTrace();
        }
    }

    public CategorySQLiteService(Connection con) {
        this.con = con;
        this.initTable();
    }

    public CategorySQLiteService(String name) {
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
    public ArrayList read() throws SQLException {
        ArrayList<Category> rows = new ArrayList<>();
        try {
            Statement st = con.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM Category");
            while (res.next()) {
                Category category = new Category(res.getString("name"));
                category.description = res.getString("description");
                rows.add(category);
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
    public void create(Object obj) {
        Category category = (Category) obj;
        try {
            PreparedStatement statement = con.prepareStatement(
                    "INSERT INTO Category (name, description)\n" +
                            "VALUES (?, ?);"
            );
            statement.setString(1, category.name);
            statement.setString(2, category.description);

            int result = statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вставку");
            e.printStackTrace();
        }
    }

    @Override
    public void update(Object obj) throws SQLException {
        Category category = (Category) obj;
        try {
            PreparedStatement statement = con.prepareStatement(
                    "UPDATE Category SET " +
                            "description = ? " +
                            "WHERE name = ? ;"
            );
            statement.setString(1, category.description);
            statement.setString(2, category.name);

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
        Category category = (Category) obj;
        try {
            PreparedStatement statement = con.prepareStatement(
                    "DELETE FROM Category WHERE name = ? ;");
            statement.setString(1, category.name);
            int result = statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит на вставку");
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public Object findOne(String key) throws SQLException {
        ArrayList<Category> rows = new ArrayList<>();
        try {
            PreparedStatement statement = con.prepareStatement(
                    "SELECT * FROM Category WHERE name = ? ;"
            );
            statement.setString(1, key);
            ResultSet res = statement.executeQuery();

            while (res.next()) {
                Category category = new Category(res.getString("name"));
                category.description = res.getString("description");
                rows.add(category);
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
        ArrayList<Category> rows = new ArrayList<>();
        try {
            Statement st = con.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM Category WHERE " + filter + " ;");
            while (res.next()) {
                Category category = new Category(res.getString("name"));
                category.description = res.getString("description");
                rows.add(category);
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
