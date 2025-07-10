package shopeazy.com.ecommerce_app.service.contracts;

import shopeazy.com.ecommerce_app.model.document.User;
import shopeazy.com.ecommerce_app.model.dto.request.SellerApprovalRequest;
import shopeazy.com.ecommerce_app.model.dto.request.SellerProfileRequest;
import shopeazy.com.ecommerce_app.model.dto.response.SellerProfileResponse;

import java.util.List;

public interface SellerProfileService {
    List<SellerProfileResponse> getAll();
    SellerProfileResponse getById(String id);

    SellerProfileResponse applyForSeller(User user, SellerProfileRequest request);

    void approveSeller(SellerApprovalRequest request);

    void rejectSeller(String sellerId, String reason);

    /*
        Change seller's status from pending to
     */
    SellerProfileResponse updateStatus(String sellerId, String status);

    List<SellerProfileResponse> bulkUpdateStatus(List<String> ids, String status);

    void bulkDelete(List<String> ids);

    void deleteAllSellers();
    void deleteSeller(String sellerId);
}
