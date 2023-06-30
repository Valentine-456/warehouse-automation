package dbService;

import java.math.BigDecimal;

public class Product {
    public Product(String name, String category_name) {
        this.category_name = category_name;
        this.name = name;
    }

    public String name;
    public String description = "";
    public String manufacturer = "";
    private int quantity = 0;
    public String category_name;
    private BigDecimal price = BigDecimal.ZERO;

    public void setQuantity(int quantity) {
        if (quantity >= 0) this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setPrice(BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) >= 0)
            this.price = price;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Product{" +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}
