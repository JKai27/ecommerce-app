package shopeazy.com.ecommerce_app.mapper;

import shopeazy.com.ecommerce_app.model.document.Seller;
import shopeazy.com.ecommerce_app.model.document.User;
import shopeazy.com.ecommerce_app.model.dto.response.SellerProfileResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SellerProfileResponseMapper {
    public static SellerProfileResponse toResponse(Seller profile, User user, long productCount, List<String> permissions) {
        SellerProfileResponse response = new SellerProfileResponse();

        response.setId(profile.getSellerId());
        response.setCompanyName(profile.getCompanyName());
        response.setSellerNumber(profile.getSellerNumber());
        response.setContactEmail(profile.getContactEmail());
        response.setSellerStatus(profile.getSellerStatus().name());
        response.setProductCount((int) productCount);
        response.setRegisteredAt(profile.getRegisteredAt());

        // Populate customer (user) details
        SellerProfileResponse.CustomerResponse customer = new SellerProfileResponse.CustomerResponse();
        customer.setId(user.getId());
        customer.setUsername(user.getUsername());
        customer.setEmail(user.getEmail());
        customer.setFirstName(user.getFirstName());
        customer.setLastName(user.getLastName());
        customer.setGender(user.getGender().name());
        customer.setImageUrl(user.getImageUrl());
        List<String> roles = user.getRoles();
        customer.setRoles(roles);
        List<String> safePermissions = permissions != null ? permissions : new ArrayList<>();
        customer.setPermissions(new ArrayList<>(safePermissions));
        customer.setStatus(user.getStatus().name());
        customer.setCreatedAt(user.getCreatedAt());
        customer.setUpdatedAt(user.getUpdatedAt());

        if (user.getAddress() != null) {
            SellerProfileResponse.AddressResponse address = new SellerProfileResponse.AddressResponse();
            address.setStreet(user.getAddress().getStreet());
            address.setCity(user.getAddress().getCity());
            address.setState(user.getAddress().getState());
            address.setZip(user.getAddress().getZip());
            address.setCountry(user.getAddress().getCountry());
            customer.setAddress(address);
        }

        response.setCustomer(customer);
        return response;
    }

    public static SellerProfileResponse toResponse(Seller profile, User user) {
        return toResponse(profile, user, 0, Collections.emptyList());
    }

}
