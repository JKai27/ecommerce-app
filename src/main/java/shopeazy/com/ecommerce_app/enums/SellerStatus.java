package shopeazy.com.ecommerce_app.enums;

public enum SellerStatus {
    PENDING, ACTIVE, REJECTED, BLOCKED;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
