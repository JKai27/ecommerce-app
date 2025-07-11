package shopeazy.com.ecommerce_app.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import shopeazy.com.ecommerce_app.exceptions.DuplicateProductException;
import shopeazy.com.ecommerce_app.exceptions.ForbiddenOperationException;
import shopeazy.com.ecommerce_app.exceptions.ResourceNotFoundException;
import shopeazy.com.ecommerce_app.mapper.ProductMapper;
import shopeazy.com.ecommerce_app.model.document.Product;
import shopeazy.com.ecommerce_app.model.dto.request.CreateProductRequest;
import shopeazy.com.ecommerce_app.model.dto.response.ProductResponseDto;
import shopeazy.com.ecommerce_app.repository.ProductRepository;
import shopeazy.com.ecommerce_app.service.contracts.UpdateProductRequestDto;
import shopeazy.com.ecommerce_app.util.ProductValidator;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements shopeazy.com.ecommerce_app.service.contracts.ProductService {
    private final ProductRepository productRepository;

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public ProductResponseDto getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product by product Id " + id + " doesn't exist"));
        return ProductMapper.mapToDto(product);
    }

    @Override
    public ProductResponseDto registerProduct(CreateProductRequest request) {
        if (productRepository.existsByNameAndSellerId(request.getName(), request.getSellerId())) {
            throw new DuplicateProductException("Product with name '" + request.getName() + "' already exists for this seller.");
        }
        Product productToRegister = new Product();


        productToRegister.setName(request.getName());
        productToRegister.setDescription(request.getDescription());
        productToRegister.setPrice(request.getPrice());
        productToRegister.setDiscount(request.getDiscount());
        productToRegister.setStockCount(request.getStockCount());
        productToRegister.setCategory(request.getCategory());
        productToRegister.setStatus(request.getStatus());
        productToRegister.setSellerId(request.getSellerId());
        productRepository.save(productToRegister);
        return ProductMapper.mapToDto(productToRegister);
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
        return ProductMapper.mapToDto(updatedProduct);
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


        return updated.stream()
                .map(ProductMapper::mapToDto)
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


    private Product applyUpdate(Product product, UpdateProductRequestDto request) {
        ProductValidator.validatePrice(request.getPrice());
        ProductValidator.validateDiscount(request.getDiscount());

        Optional.ofNullable(request.getName()).ifPresent(product::setName);
        Optional.ofNullable(request.getDescription()).ifPresent(product::setDescription);
        Optional.ofNullable(request.getPrice()).ifPresent(product::setPrice);
        Optional.ofNullable(request.getDiscount()).ifPresent(product::setDiscount);
        Optional.ofNullable(request.getStockCount()).ifPresent(product::setStockCount);
        Optional.ofNullable(request.getCategory()).ifPresent(product::setCategory);
        Optional.ofNullable(request.getStatus()).ifPresent(product::setStatus);
        return product;
    }
}
