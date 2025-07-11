package shopeazy.com.ecommerce_app.service.implementation;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import shopeazy.com.ecommerce_app.exceptions.ForbiddenOperationException;
import shopeazy.com.ecommerce_app.exceptions.ResourceNotFoundException;
import shopeazy.com.ecommerce_app.model.document.Product;
import shopeazy.com.ecommerce_app.model.document.User;
import shopeazy.com.ecommerce_app.repository.ProductRepository;
import shopeazy.com.ecommerce_app.repository.UserRepository;
import shopeazy.com.ecommerce_app.service.contracts.ProductImagesManagementService;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductImagesManagementServiceImpl implements ProductImagesManagementService {
    private final ProductRepository productRepository;
    private final GridFSBucket gridFSBucket;
    private final GridFsTemplate gridFsTemplate;
    private final UserRepository userRepository;

    public List<String> uploadImages(List<MultipartFile> files, String productId, String sellerEmail) throws BadRequestException {
        List<String> imageUrls = new ArrayList<>();

        // Only the valid Seller/Owner can upload images of relevant product
        validateProductOwner(productId, sellerEmail);

        Product product = productRepository.findById(productId).orElseThrow(ResourceNotFoundException::new);


        for (MultipartFile file : files) {

            // Validate file type
            if (!Objects.requireNonNull(file.getContentType()).matches("image/(jpeg|png)|application/pdf")) {
                throw new BadRequestException("Only JPG, PNG, and PDF files are allowed.");
            }

            // Validate file size (max 5MB, for example)
            if (file.getSize() > 5 * 1024 * 1024) {
                throw new BadRequestException("File size must be less than 5MB.");
            }

            try {
                // Save the files in GridFS
                ObjectId fileId = storeFileInGridFS(file);
                log.info("Uploading image for fileId={}", fileId);

                // Generating an URL
                String fileUrl = generateFileUrl(fileId);
                imageUrls.add(fileUrl);

            } catch (IOException | NoSuchAlgorithmException exception) {
                throw new BadRequestException("Error uploading file: " + exception.getMessage());
            }

            // Save images to product and in DB
            product.setImages(imageUrls);
            productRepository.save(product);
        }
        return imageUrls;
    }

    private void validateProductOwner(String productId, String sellerEmail) {
        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + sellerEmail + " not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + productId + " not found"));
        if (!product.getSellerId().equals(seller.getId())) {
            throw new AccessDeniedException("Seller does not own this product.");
        }
    }


    private ObjectId storeFileInGridFS(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        String fileHash = computeSHA256HASH(file);

        if (fileAlreadyExists(fileHash)) {
            throw new ForbiddenOperationException("Duplicate file upload detected.");
        }
        Document metadata = new Document()
                .append("fileHash", fileHash)
                .append("contentType", file.getContentType());

        String filename = file.getOriginalFilename();
        if (filename == null || filename.trim().isEmpty()) {
            filename = UUID.randomUUID().toString();
        }

        try (InputStream inputStream = file.getInputStream()) {
            return gridFSBucket.uploadFromStream(filename, inputStream,
                    new GridFSUploadOptions().metadata(metadata));
        }
    }

    private boolean fileAlreadyExists(String fileHash) {
        Query query = Query.query(Criteria.where("metadata.fileHash").is(fileHash));
        GridFSFile existingFile = gridFsTemplate.findOne(query);
        log.info("Checking for the existing file with hash={} -> found={}", fileHash, existingFile);
        return existingFile != null;
    }


    private String computeSHA256HASH(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(file.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    // Generate the URL for accessing the file (could be a path or an S3-like URL)
    private String generateFileUrl(ObjectId fileId) {
        return "https://shop-eazy.com/api/files/" + fileId.toHexString();
    }

}
