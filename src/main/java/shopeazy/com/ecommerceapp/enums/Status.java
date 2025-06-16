package shopeazy.com.ecommerceapp.enums;

public enum Status {
    ACTIVE,
    BLOCKED,
    INACTIVE;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
