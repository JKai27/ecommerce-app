package shopeazy.com.ecommerce_app.product.validator;

public class ProductValidator {
    private ProductValidator() {
    }

    public static void validatePrice(Double price) {
        if (price!=null && price <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero.");
        }
    }

    public static void validateDiscount(Double discount) {
        if (discount == null) return;
        if (discount < 0 || discount > 100) {
            throw new IllegalArgumentException("Discount must be between 0 and 100.");
        }
    }
}
