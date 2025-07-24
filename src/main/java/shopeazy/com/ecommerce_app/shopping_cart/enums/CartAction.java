package shopeazy.com.ecommerce_app.shopping_cart.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CartAction {
    ADD,
    REMOVE;

    @JsonCreator
    public static CartAction fromString(String value) {
        return value == null ? null : CartAction.valueOf(value.toUpperCase());

    }
}