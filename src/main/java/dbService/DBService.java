package dbService;

import java.util.ArrayList;

public interface DBService {
    public ArrayList read();

    public void create(Object obj);

    public void update(Object obj);

    public void delete(Object obj);

    public Object findOne(String key);

    public ArrayList listByCriteria(String filter);
}
