package shopeazy.com.ecommerceapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import shopeazy.com.ecommerceapp.exceptions.SellerAlreadyExistsException;
import shopeazy.com.ecommerceapp.model.document.User;
import shopeazy.com.ecommerceapp.model.dto.request.*;
import shopeazy.com.ecommerceapp.model.dto.response.ApiResponse;
import shopeazy.com.ecommerceapp.model.dto.response.SellerProfileResponse;
import shopeazy.com.ecommerceapp.service.contracts.SellerProfileService;
import shopeazy.com.ecommerceapp.service.contracts.UserService;

import java.security.Principal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sellers")
public class SellerController {
    private final SellerProfileService sellerProfileService;
    private final UserService userService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<SellerProfileResponse>> getAll() {
        List<SellerProfileResponse> sellerProfiles = sellerProfileService.getAll();
        return ResponseEntity.ok(sellerProfiles);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/{sellerId}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable("sellerId") String sellerId) {
        Map<String, Object> response = new HashMap<>();
        SellerProfileResponse sellerProfile = sellerProfileService.getById(sellerId);
        response.put("sellerProfile", sellerProfile);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<SellerProfileResponse>> applyForSeller(@RequestBody SellerProfileRequest request, Principal principal) {
        try {
            User user = userService.getUserByPrincipal(principal);
            SellerProfileResponse profile = sellerProfileService.applyForSeller(user, request);

            ApiResponse<SellerProfileResponse> response = new ApiResponse<>(
                    true,
                    "You have successfully applied for the seller role.",
                    profile,
                    Instant.now()
            );
            return ResponseEntity.ok(response);
        } catch (SellerAlreadyExistsException e) {
            ApiResponse<SellerProfileResponse> errorResponse = new ApiResponse<>(
                    false,
                    "You have already applied for the seller profile.",
                    null,
                    Instant.now()
            );

            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/approve")
    public ResponseEntity<String> approveSeller(@RequestBody SellerApprovalRequest request) {
        sellerProfileService.approveSeller(request);
        log.info("Seller approved for the seller role: {}", request.getUserEmail());

        return ResponseEntity.ok("You have successfully approved the seller role");
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/{id}/rejection")
    public ResponseEntity<ApiResponse<String>> rejectSeller(@PathVariable String id,
                                                            @Valid @RequestBody SellerRejectionRequest reason) {
        sellerProfileService.rejectSeller(id, reason.getReason());
        log.info("Seller with ID {} rejected. Reason: {}", id, reason.getReason());
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Seller rejected successfully", null, Instant.now())
        );
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<SellerProfileResponse>> updateStatus(@PathVariable String id,
                                                                           @Valid @RequestBody StatusUpdateRequest request) {
        log.info("Request to update status of seller {}", id);
        SellerProfileResponse response = sellerProfileService.updateStatus(id, request.getStatus());
        log.info("Status of seller {} updated", id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Status of seller updated", response, Instant.now())
        );
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/bulk-status")
    public ResponseEntity<ApiResponse<List<SellerProfileResponse>>> updateSellerStatusesInBulk(
            @RequestBody BulkStatusRequest request) {
        try {
            List<SellerProfileResponse> updatedSellers =
                    sellerProfileService.bulkUpdateStatus(request.getIds(), request.getStatus());

            ApiResponse<List<SellerProfileResponse>> response = new ApiResponse<>(
                    true,
                    "Successfully updated statuses for " + updatedSellers.size() + " sellers.",
                    updatedSellers,
                    Instant.now()
            );

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            ApiResponse<List<SellerProfileResponse>> errorResponse = new ApiResponse<>(
                    false,
                    "Invalid status value: " + request.getStatus(),
                    null,
                    Instant.now()
            );
            return ResponseEntity.badRequest().body(errorResponse);

        } catch (Exception e) {
            ApiResponse<List<SellerProfileResponse>> errorResponse = new ApiResponse<>(
                    false,
                    "Failed to update sellers in bulk.",
                    null,
                    Instant.now()
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{sellerId}")
    public ResponseEntity<ApiResponse<String>> deleteSeller(@PathVariable String sellerId) {
        sellerProfileService.deleteSeller(sellerId);
        ApiResponse<String> response = new ApiResponse<>(
                true,
                "Seller deleted successfully.",
                sellerId,
                Instant.now()
        );
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/bulk")
    public ResponseEntity<ApiResponse<List<String>>> bulkDelete(@RequestBody IdListRequest request) {
        sellerProfileService.bulkDelete(request.getIds());

        ApiResponse<List<String>> response = new ApiResponse<>(
                true,
                "Successfully deleted " + request.getIds().size() + " sellers.",
                request.getIds(),
                Instant.now()
        );
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> deleteAllSellers() {
        sellerProfileService.deleteAllSellers();
        ApiResponse<String> response = new ApiResponse<>(
                true,
                "All sellers deleted successfully.",
                null,
                Instant.now()
        );
        return ResponseEntity.ok(response);
    }
}
