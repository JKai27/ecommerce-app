package shopeazy.com.ecommerce_app.common.enums;

public enum Status {
    ACTIVE,
    BLOCKED,
    INACTIVE;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
