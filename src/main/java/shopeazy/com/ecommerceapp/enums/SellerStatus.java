package shopeazy.com.ecommerceapp.enums;

public enum SellerStatus {
    PENDING, ACTIVE, REJECTED, BLOCKED;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
