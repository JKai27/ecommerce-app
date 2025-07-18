package shopeazy.com.ecommerce_app.seller.enums;

public enum SellerStatus {
    PENDING, ACTIVE, REJECTED, BLOCKED;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
