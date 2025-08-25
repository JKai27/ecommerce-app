package shopeazy.com.ecommerce_app.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import shopeazy.com.ecommerce_app.common.model.Address;

/**
 * Request DTO for creating a new order from shopping cart.
 * Contains addresses and payment information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    /**
     * Shipping address for the order
     */
    @NotNull(message = "Shipping address is required")
    @Valid
    private Address shippingAddress;

    /**
     * Billing address (optional: will use shipping if not provided)
     */
    @Valid
    private Address billingAddress;

    /**
     * Payment method/transaction ID from payment gateway
     */
    @NotBlank(message = "Payment transaction ID is required")
    private String paymentTransactionId;

    /**
     * Optional notes or special instructions
     */
    private String notes;

    /**
     * Flag to use shipping address as billing address
     */
    private boolean useSameAddressForBilling = true;
}