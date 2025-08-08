package shopeazy.com.ecommerce_app.seller.service;

import shopeazy.com.ecommerce_app.user.model.User;
import shopeazy.com.ecommerce_app.seller.dto.SellerApprovalRequest;
import shopeazy.com.ecommerce_app.seller.dto.SellerProfileRequest;
import shopeazy.com.ecommerce_app.seller.dto.SellerProfileResponse;

import java.util.List;

public interface SellerProfileService {
    List<SellerProfileResponse> getAll();
    SellerProfileResponse getById(String id);

    SellerProfileResponse applyForSeller(User user, SellerProfileRequest request);

    SellerProfileResponse approveSeller(SellerApprovalRequest request);

    void rejectSeller(String sellerId, String reason);
    /*
            Change seller's status from pending to
         */
    SellerProfileResponse updateStatus(String sellerId, String status);

    List<SellerProfileResponse> bulkUpdateStatus(List<String> ids, String status);

    void bulkDelete(List<String> ids);

    void deleteAllSellers();
    void deleteSeller(String sellerId);

    SellerProfileResponse getByUserId(String id);
    SellerProfileResponse updateOwnProfile(SellerProfileRequest request, String userId);
}
