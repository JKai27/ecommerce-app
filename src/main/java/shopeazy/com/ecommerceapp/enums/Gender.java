package shopeazy.com.ecommerceapp.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {
    MALE, FEMALE, DIVERSE;

    @JsonCreator
    public static Gender fromString(String gender) {
        try {
            return Gender.valueOf(gender.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid gender value " + gender);
        }
    }

    @JsonValue
    public String toValue() {
        return this.name().toLowerCase();
    }
}