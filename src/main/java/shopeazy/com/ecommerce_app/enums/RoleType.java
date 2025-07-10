package shopeazy.com.ecommerce_app.enums;

public enum RoleType {
    ROLE_ADMIN,
    ROLE_USER,
    ROLE_SELLER;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
