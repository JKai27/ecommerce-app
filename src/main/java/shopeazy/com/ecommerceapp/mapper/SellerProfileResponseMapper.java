package shopeazy.com.ecommerceapp.mapper;

import shopeazy.com.ecommerceapp.model.document.Seller;
import shopeazy.com.ecommerceapp.model.document.User;
import shopeazy.com.ecommerceapp.model.dto.response.SellerProfileResponse;

public class SellerProfileResponseMapper {
    public static SellerProfileResponse toResponse(Seller profile, User user, long productCount) {
        SellerProfileResponse response = new SellerProfileResponse();

        response.setId(profile.getSellerId());
        response.setCompanyName(profile.getCompanyName());
        response.setSellerNumber(profile.getSellerNumber());
        response.setContactEmail(profile.getContactEmail());
        response.setStatus(profile.getStatus().name());
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
        customer.setRoles(user.getRoles());
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
        return toResponse(profile, user, 0); // fallback count
    }

}
