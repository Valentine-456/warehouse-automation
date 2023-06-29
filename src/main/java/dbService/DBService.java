package dbService;

import java.sql.SQLException;
import java.util.ArrayList;

public interface DBService {
    public ArrayList read() throws SQLException;

    public void create(Object obj) throws SQLException;

    public void update(Object obj) throws SQLException;

    public void delete(Object obj) throws SQLException;

    public Object findOne(String key) throws SQLException;

    public ArrayList listByCriteria(String filter) throws SQLException;
}
