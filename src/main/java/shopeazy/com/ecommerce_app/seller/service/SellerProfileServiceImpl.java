package shopeazy.com.ecommerce_app.seller.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import shopeazy.com.ecommerce_app.security.enums.RoleType;
import shopeazy.com.ecommerce_app.seller.enums.SellerStatus;
import shopeazy.com.ecommerce_app.security.exception.InvalidEmailException;
import shopeazy.com.ecommerce_app.common.exception.ResourceNotFoundException;
import shopeazy.com.ecommerce_app.seller.exception.SellerAccountForTheCompanyNameAlreadyExistsException;
import shopeazy.com.ecommerce_app.seller.exception.SellerAlreadyExistsException;
import shopeazy.com.ecommerce_app.seller.mapper.SellerProfileResponseMapper;
import shopeazy.com.ecommerce_app.security.model.Permission;
import shopeazy.com.ecommerce_app.seller.model.Seller;
import shopeazy.com.ecommerce_app.user.model.User;
import shopeazy.com.ecommerce_app.seller.dto.SellerApprovalRequest;
import shopeazy.com.ecommerce_app.seller.dto.SellerProfileRequest;
import shopeazy.com.ecommerce_app.seller.dto.SellerProfileResponse;
import shopeazy.com.ecommerce_app.product.repository.ProductRepository;
import shopeazy.com.ecommerce_app.seller.repository.SellerProfileRepository;
import shopeazy.com.ecommerce_app.user.repository.UserRepository;
import shopeazy.com.ecommerce_app.seller.service.SellerNumberService;
import shopeazy.com.ecommerce_app.security.service.RoleAssignmentService;
import shopeazy.com.ecommerce_app.seller.service.SellerProfileService;
import shopeazy.com.ecommerce_app.security.service.PermissionResolverService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class SellerProfileServiceImpl implements SellerProfileService {
    private final UserRepository userRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final SellerNumberService sellerNumberService;
    private final ProductRepository productRepository;
    private final RoleAssignmentService roleAssignmentService;
    private final PermissionResolverService permissionResolverService;

    /*
        Get all sellers with their respective customer data
     */
    @Override
    public List<SellerProfileResponse> getAll() {
        List<Seller> sellers = sellerProfileRepository.findAll();

        return sellers.stream()
                .map(seller -> {
                    User user = userRepository.findById(seller.getUserId())
                            .orElseThrow(ResourceNotFoundException::new);

                    long productCount = productRepository.countBySellerId(seller.getSellerId());
                    Set<String> resolvePermissions = permissionResolverService.resolvePermissions(user);

                    return SellerProfileResponseMapper.toResponse(
                            seller,
                            user,
                            productCount,
                            new ArrayList<>(resolvePermissions)
                    );
                })
                .toList(); // or .collect(Collectors.toList()) if using Java < 16
    }

    /*
        Get a single seller with its respective customer data
     */
    @Override
    public SellerProfileResponse getById(String id) {
        Seller seller = sellerProfileRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        User user = userRepository.findById(seller.getUserId())
                .orElseThrow(ResourceNotFoundException::new);

        return SellerProfileResponseMapper.toResponse(seller, user);
    }

    /*
        A user can apply for become a seller through this endpoint.
     */

    @Override
    public SellerProfileResponse applyForSeller(User user, SellerProfileRequest request) {
        checkSellerDoesNotExist(user.getEmail());

        if (!isCompanyNameUnique(request.getCompanyName())) {
            throw new SellerAccountForTheCompanyNameAlreadyExistsException(
                    "A Seller account under the company name " + request.getCompanyName() + " already exists."
            );
        }
        if (request.getContactEmail() != null && sellerProfileRepository.existsByContactEmail(request.getContactEmail())) {
            throw new InvalidEmailException("Contact email address already in use. Please choose another one.");
        }

        Seller seller = new Seller();
        seller.setCompanyName(request.getCompanyName());
        seller.setContactEmail(request.getContactEmail());
        seller.setUserId(user.getId());
        seller.setRegisteredAt(Instant.now());
        seller.setSellerStatus(SellerStatus.PENDING);
        seller.setProductCount(0);

        int nextSeq = sellerNumberService.getNextSequence("sellerNumber");
        String sellerNumber = String.format("%06d", nextSeq);
        seller.setSellerNumber(sellerNumber);

        log.info("Saving seller with contactEmail: {}", seller.getContactEmail());
        Seller savedProfile = sellerProfileRepository.save(seller);
        log.info("Saved seller: {}", savedProfile);
        return SellerProfileResponseMapper.toResponse(savedProfile, user);
    }

    private boolean isCompanyNameUnique(String companyName) {
        List<Seller> existingSellers = sellerProfileRepository.findByCompanyName(companyName);
        if (!existingSellers.isEmpty()) {
            log.error("Seller account already exists for company name: {}", companyName);
            throw new SellerAccountForTheCompanyNameAlreadyExistsException(
                    "A Seller account under the company name " + companyName + " already exists.");
        }
        return true;
    }

    /*
        Admin approves the request from the user to become a seller
     */
    @Override
    public void approveSeller(SellerApprovalRequest request) {
        Seller seller = sellerProfileRepository.findByContactEmail(request.getContactEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found with email: " + request.getContactEmail()));
        seller.setSellerStatus(SellerStatus.ACTIVE);
        sellerProfileRepository.save(seller);
        User user = addSellerRoleAndPermissionsToTheApprovedUserForTheSellerProfile(request);
        userRepository.save(user);
    }

    private User addSellerRoleAndPermissionsToTheApprovedUserForTheSellerProfile(SellerApprovalRequest request) {
        User user = userRepository.findByEmail(request.getUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getUserEmail()));

        List<Permission> sellerPermissions = List.of(
                new Permission("READ"),
                new Permission("WRITE"),
                new Permission("CREATE")
        );
        roleAssignmentService.createRoleWithPermissions(RoleType.ROLE_SELLER.name(), sellerPermissions);

        // Assigning Seller-Role to the requested user.
        roleAssignmentService.assignRoleToUser(user, RoleType.ROLE_SELLER.name());
        log.info("Assigned ROLE_SELLER to user {} ", user.getEmail());
        return user;
    }


    /*
        Admin can reject the request from the user to become a seller
     */

    @Override
    public void rejectSeller(String sellerId, String reason) {
        Seller profile = sellerProfileRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        profile.setSellerStatus(SellerStatus.REJECTED);
        // mention reason?
        sellerProfileRepository.save(profile);
    }

    /*
        Delete a seller by its ID
     */

    @Override
    public void deleteSeller(String sellerId) {
        Seller seller = sellerProfileRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        sellerProfileRepository.delete(seller);
    }

    /*
        Change seller's status from pending to
     */
    @Override
    public SellerProfileResponse updateStatus(String sellerId, String status) {
        Seller seller = sellerProfileRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found with ID: " + sellerId));
        User customer = userRepository.findById(seller.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + seller.getUserId()));

        seller.setSellerStatus(SellerStatus.valueOf(status.toUpperCase()));
        sellerProfileRepository.save(seller);
        return SellerProfileResponseMapper.toResponse(seller, customer);
    }


    /*
         Bulk Endpoints
     */

    @Override
    public List<SellerProfileResponse> bulkUpdateStatus(List<String> ids, String status) {
        SellerStatus newStatus = SellerStatus.valueOf(status.toUpperCase());

        List<Seller> sellers = sellerProfileRepository.findAllById(ids);

        for (Seller seller : sellers) {
            seller.setSellerStatus(newStatus);
        }

        sellerProfileRepository.saveAll(sellers);

        List<SellerProfileResponse> responses = new ArrayList<>();
        for (Seller seller : sellers) {
            User customer = userRepository.findById(seller.getUserId())
                    .orElseThrow(() -> new RuntimeException("Customer not found for seller: " + seller.getSellerId()));
            SellerProfileResponse response = SellerProfileResponseMapper.toResponse(seller, customer);
            responses.add(response);
        }

        return responses;
    }

    @Override
    public void bulkDelete(List<String> ids) {
        List<Seller> sellers = sellerProfileRepository.findAllById(ids);

        if (sellers.size() != ids.size()) {
            throw new ResourceNotFoundException("Some seller IDs were not found.");
        }

        sellerProfileRepository.deleteAll(sellers);
    }


    @Override
    public void deleteAllSellers() {
        sellerProfileRepository.deleteAll();
        sellerNumberService.resetSequence("sellerNumber");
    }


    private void checkSellerDoesNotExist(String userEmail) {
        if (sellerProfileRepository.existsByContactEmail(userEmail)) {
            throw new SellerAlreadyExistsException("Seller already exists under this email: " + userEmail);
        }
    }
}
