package shopeazy.com.ecommerceapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import shopeazy.com.ecommerceapp.exceptions.SellerAlreadyExistsException;
import shopeazy.com.ecommerceapp.model.document.User;
import shopeazy.com.ecommerceapp.model.dto.request.BulkStatusRequest;
import shopeazy.com.ecommerceapp.model.dto.request.IdListRequest;
import shopeazy.com.ecommerceapp.model.dto.request.SellerProfileRequest;
import shopeazy.com.ecommerceapp.model.dto.request.StatusUpdateRequest;
import shopeazy.com.ecommerceapp.model.dto.response.SellerProfileResponse;
import shopeazy.com.ecommerceapp.service.contracts.SellerProfileService;
import shopeazy.com.ecommerceapp.service.contracts.UserService;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sellers")
public class SellerController {
    private final SellerProfileService sellerProfileService;
    private final UserService userService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<?> getAll() {
        Map<String, Object> response = new HashMap<>();
        List<SellerProfileResponse> sellerProfiles = sellerProfileService.getAll();
        response.put("sellerProfiles", sellerProfiles);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/{sellerId}")
    public ResponseEntity<?> getById(@PathVariable("sellerId") String sellerId) {
        Map<String, Object> response = new HashMap<>();
        SellerProfileResponse sellerProfile = sellerProfileService.getById(sellerId);
        response.put("sellerProfile", sellerProfile);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/apply")
    public ResponseEntity<?> applyForSeller(@RequestBody SellerProfileRequest request, Principal principal) {
        try {
            User user = userService.getUserByPrincipal(principal);
            Map<String, Object> response = new HashMap<>();
            SellerProfileResponse profile = sellerProfileService.applyForSeller(user, request);
            response.put("You have successfully applied for the seller profile", profile);
            return ResponseEntity.ok(response);
        } catch (SellerAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("You have already applied for the seller profile");
        }
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/approve/{id}")
    public ResponseEntity<?> approveSeller(@PathVariable String id) {
        sellerProfileService.approveSeller(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/reject/{id}")
    public ResponseEntity<?> rejectSeller(@PathVariable String id, @RequestBody String reason) {
        sellerProfileService.rejectSeller(id, reason);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable String id, @RequestBody StatusUpdateRequest request) {
        sellerProfileService.updateStatus(id, request.getStatus());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/bulk-status")
    public ResponseEntity<?> updateSellerStatusesInBulk(@RequestBody BulkStatusRequest request) {
        try {
            List<SellerProfileResponse> updatedSellers = sellerProfileService.bulkUpdateStatus(request.getIds(), request.getStatus());
            return ResponseEntity.ok(updatedSellers);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status value: " + request.getStatus());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to update sellers in bulk.");
        }
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("{sellerId}/delete")
    public ResponseEntity<?> deleteSeller(@PathVariable String sellerId) {
        Map<String, Object> response = new HashMap<>();
        sellerProfileService.deleteSeller(sellerId);
        response.put("Deleted Seller", sellerId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/bulk-delete")
    public ResponseEntity<?> bulkDelete(@RequestBody IdListRequest request) {
        sellerProfileService.bulkDelete(request.getIds());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/deleteAll")
    public ResponseEntity<?> deleteAllSellers() {
        sellerProfileService.deleteAllSellers();
        return ResponseEntity.noContent().build();
    }
}
