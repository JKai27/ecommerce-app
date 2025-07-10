package shopeazy.com.ecommerce_app.enums;

public enum Status {
    ACTIVE,
    BLOCKED,
    INACTIVE;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
