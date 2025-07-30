package shopeazy.com.ecommerce_app.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import shopeazy.com.ecommerce_app.product.dto.*;
import shopeazy.com.ecommerce_app.product.enums.ProductStatus;
import shopeazy.com.ecommerce_app.product.exception.DuplicateProductException;
import shopeazy.com.ecommerce_app.product.exception.ProductOutOfStockException;
import shopeazy.com.ecommerce_app.security.exception.ForbiddenOperationException;
import shopeazy.com.ecommerce_app.common.exception.ResourceNotFoundException;
import shopeazy.com.ecommerce_app.product.mapper.ProductMapper;
import shopeazy.com.ecommerce_app.product.model.Product;
import shopeazy.com.ecommerce_app.product.repository.ProductRepository;
import shopeazy.com.ecommerce_app.product.validator.ProductValidator;
import shopeazy.com.ecommerce_app.seller.model.Seller;
import shopeazy.com.ecommerce_app.seller.repository.SellerProfileRepository;
import shopeazy.com.ecommerce_app.common.UniqueReadableNumberService;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final UniqueReadableNumberService uniqueReadableNumberService;
    private final ModelMapper modelMapper;

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public List<ProductResponseDto> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(product -> {
                    Seller seller = sellerProfileRepository.findById(product.getSellerId())
                            .orElse(null); // Handle cases where seller might not exist
                    return ProductMapper.mapToDto(product, seller);
                })
                .toList();
    }

    @Override
    public ProductResponseDto getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product by product Id " + id + " doesn't exist"));

        Seller seller = sellerProfileRepository.findById(product.getSellerId())
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found for product " + id));

        return ProductMapper.mapToDto(product, seller);
    }

    @Override
    public ProductResponseDto registerProduct(CreateProductRequest request) {
        if (productRepository.existsByNameAndSellerId(request.getName(), request.getSellerId())) {
            throw new DuplicateProductException("Product with name '" + request.getName() + "' already exists for this seller.");
        }
        Product productToRegister = new Product();

        int sequence = uniqueReadableNumberService.getNextSequence("product");
        String productNumber = String.format("%06d", sequence); // z. B. "000123"
        productToRegister.setProductNumber(productNumber);
        productToRegister.setName(request.getName());
        productToRegister.setDescription(request.getDescription());
        productToRegister.setPrice(request.getPrice());
        productToRegister.setDiscount(request.getDiscount());
        productToRegister.setStockCount(request.getStockCount());
        productToRegister.setCategory(request.getCategory());
        productToRegister.setStatus(request.getStatus());
        productToRegister.setSellerId(request.getSellerId());
        productRepository.save(productToRegister);

        Seller seller = sellerProfileRepository.findById(request.getSellerId())
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        return ProductMapper.mapToDto(productToRegister, seller);
    }

    @Override
    public ProductResponseDto updateOwnProduct(String sellerId, UpdateProductRequestDto request) throws ForbiddenOperationException {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + request.getProductId() + " doesn't exist"));
        if (!product.getSellerId().equals(sellerId)) {
            throw new ForbiddenOperationException("You cannot update this product as you are not the owner.");
        }
        applyUpdate(product, request);

        Product updatedProduct = productRepository.save(product);

        Seller seller = sellerProfileRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        return ProductMapper.mapToDto(updatedProduct, seller);
    }

    @Override
    public List<ProductResponseDto> updateOwnProductsInBulk(String sellerId, List<UpdateProductRequestDto> requestList) {
        List<Product> toUpdate = requestList.stream()
                .map(request -> productRepository.findById(request.getProductId())
                        .filter(product -> product.getSellerId().equals(sellerId))
                        .map(product -> applyUpdate(product, request))
                        .orElseGet(() -> {
                            log.warn("Skipping productId={} (not found or forbidden)", request.getProductId());
                            return null;
                        })
                )
                .filter(Objects::nonNull)
                .toList();
        List<Product> updated = productRepository.saveAll(toUpdate);

        Seller seller = sellerProfileRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        return updated.stream()
                .map(product -> ProductMapper.mapToDto(product, seller))
                .toList();
    }

    @Override
    public ProductResponseDto updateProductStatus(UpdateProductStatusRequest request) {


        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + request.getProductId() + " doesn't exist"));

        product.setStatus(request.getStatus());
        log.info("Updating status of product with id {} to status {}", request.getProductId(), request.getStatus());

        return modelMapper.map(productRepository.save(product), ProductResponseDto.class);
    }

    @Override
    public List<ProductResponseDto> bulkUpdateProductStatus(BulkUpdateProductStatusRequest request) {
        List<Product> products = productRepository.findAllById(request.getProductIds());

        if (products.isEmpty()) {
            throw new ResourceNotFoundException("No matching products found.");
        }

        products.forEach(product -> product.setStatus(request.getStatus()));
        log.info("Updating status for products: {} to status: {}",
                products.stream().map(Product::getId).toList(),request.getStatus());

        List<Product> saved = productRepository.saveAll(products);

        return saved.stream()
                .map(product -> modelMapper.map(product, ProductResponseDto.class))
                .toList();
    }

    @Override
    public List<ProductResponseDto> bulkUpdateMultipleProductStatus(BulkUpdateMultipleProductStatusRequest request) {
        List<String> productIds = request.getUpdates().stream()
                .map(UpdateProductStatusRequest::getProductId)
                .toList();

        List<Product> products = productRepository.findAllById(productIds);

        if (products.isEmpty()) {
            throw new ResourceNotFoundException("No matching products found.");
        }

        Map<String, ProductStatus> statusMap = request.getUpdates().stream()
                .collect(Collectors.toMap(UpdateProductStatusRequest::getProductId, UpdateProductStatusRequest::getStatus));

        products.forEach(product -> {
            ProductStatus newStatus = statusMap.get(product.getId());
            if (newStatus != null) {
                product.setStatus(newStatus);
            }
        });

        log.info("Updated statuses for products: {}",
                products.stream().map(Product::getId).toList());

        List<Product> saved = productRepository.saveAll(products);

        return saved.stream()
                .map(product -> modelMapper.map(product, ProductResponseDto.class))
                .toList();
    }


    @Override
    public void deleteProductById(String productId) {
        productRepository.deleteById(productId);
    }

    @Override
    public void deleteAllProductsBySellerId(String sellerId) {
        List<Product> products = productRepository.findBySellerId(sellerId);
        if (products.isEmpty()) {
            log.info("No products found for sellerId={}", sellerId);
            return;
        }
        productRepository.deleteAll(products);
        log.info("Deleted {} products for sellerId={}", products.size(), sellerId);
    }

    @Override
    public ProductAvailabilityResponse checkProductAvailability(String productId) {
        log.info("Checking product availability for productId={}", productId);

        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product with id " + productId + " doesn't exist"));

        if (product.getStockCount() < 1) {
            throw new ProductOutOfStockException("Product not available");
        }

        ProductAvailabilityResponse response = new ProductAvailabilityResponse();


        response.setAvailable(true);
        response.setProductStockCount(product.getStockCount());
        response.setMessage("Product with id " + productId + " is available in stock");

        log.info("Product availability response: {}", response);
        return response;
    }

    @Override
    public void validateRequestedQuantity(String productId, int requestedQty) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.getStockCount() < requestedQty) {
            throw new ProductOutOfStockException("Not enough stock available");
        }
    }

    @Override
    public void restoreStock(String productId, int quantityToRestore) {
        Product product = productRepository.findById(productId).orElseThrow(ResourceNotFoundException::new);
        int updatedStock = product.getStockCount() + quantityToRestore;
        product.setStockCount(updatedStock);
        productRepository.save(product);
    }


    private Product applyUpdate(Product product, UpdateProductRequestDto request) {
        ProductValidator.validatePrice(request.getPrice());
        ProductValidator.validateDiscount(request.getDiscount());

        Optional.ofNullable(request.getName()).ifPresent(product::setName);
        Optional.ofNullable(request.getDescription()).ifPresent(product::setDescription);
        Optional.ofNullable(request.getPrice()).ifPresent(product::setPrice);
        Optional.ofNullable(request.getDiscount()).ifPresent(product::setDiscount);
        Optional.of(request.getStockCount()).ifPresent(product::setStockCount);
        Optional.ofNullable(request.getCategory()).ifPresent(product::setCategory);
        Optional.ofNullable(request.getStatus()).ifPresent(product::setStatus);
        return product;
    }
}
