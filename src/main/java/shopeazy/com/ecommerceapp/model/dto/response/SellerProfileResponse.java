package shopeazy.com.ecommerceapp.model.dto.response;

import lombok.Data;
import shopeazy.com.ecommerceapp.enums.Status;

import java.time.Instant;
import java.util.List;

@Data
public class SellerProfileResponse {
    private String id;
    private String companyName;
    private String sellerNumber;
    private String contactEmail;
    private String status;
    private int productCount;
    private Instant registeredAt;

    private CustomerResponse customer;

    @Data
    public static class CustomerResponse {
        private String id;
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private String gender;
        private String imageUrl;
        private AddressResponse address;
        private List<String> roles;
        private List<String> permissions;
        private String status;
        private Instant createdAt;
        private Instant updatedAt;
    }

    @Data
    public static class AddressResponse {
        private String street;
        private String city;
        private String state;
        private String zip;
        private String country;
    }
}
