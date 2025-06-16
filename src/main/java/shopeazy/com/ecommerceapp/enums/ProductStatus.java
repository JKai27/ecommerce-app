package shopeazy.com.ecommerceapp.enums;

public enum ProductStatus {
    ACTIVE,
    BLOCKED,
    INACTIVE;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
