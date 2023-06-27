package dbService;

public class StorePOJO {
    String productName;
    String categoryName;
    int number;
    Object pojo;

    public StorePOJO(String productName, String categoryName, int number, Object pojo) {
        this.categoryName = categoryName;
        this.productName = productName;
        this.number = number;
        this.pojo = pojo;
    }
}
