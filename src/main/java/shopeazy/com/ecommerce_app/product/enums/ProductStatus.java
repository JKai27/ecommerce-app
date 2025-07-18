package shopeazy.com.ecommerce_app.product.enums;

public enum ProductStatus {
    ACTIVE,
    BLOCKED,
    INACTIVE;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
