package shopeazy.com.ecommerceapp.service.serviceImplementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shopeazy.com.ecommerceapp.exceptions.DuplicateProductException;
import shopeazy.com.ecommerceapp.exceptions.ForbiddenOperationException;
import shopeazy.com.ecommerceapp.exceptions.ResourceNotFoundException;
import shopeazy.com.ecommerceapp.mapper.ProductMapper;
import shopeazy.com.ecommerceapp.model.document.Product;
import shopeazy.com.ecommerceapp.model.dto.request.CreateProductRequest;
import shopeazy.com.ecommerceapp.model.dto.response.ProductResponseDto;
import shopeazy.com.ecommerceapp.repository.ProductRepository;
import shopeazy.com.ecommerceapp.service.contracts.ProductService;
import shopeazy.com.ecommerceapp.service.contracts.UpdateProductRequestDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
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
        Product product = new Product();


        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setDiscount(request.getDiscount());
        product.setStockCount(request.getStockCount());
        product.setCategory(request.getCategory());
        product.setStatus(request.getStatus());
        product.setSellerId(request.getSellerId());
        productRepository.save(product);
        return ProductMapper.mapToDto(product);
    }

    @Override
    public ProductResponseDto updateOwnProduct(String sellerId, UpdateProductRequestDto request) throws ForbiddenOperationException {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + request.getProductId() + " doesn't exist"));
        if (!product.getSellerId().equals(sellerId)) {
            throw new ForbiddenOperationException("You cannot update this product as you are not the owner.");
        }
        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getDiscount() != null) product.setDiscount(request.getDiscount());
        if (request.getStockCount() != null) product.setStockCount(request.getStockCount());
        if (request.getCategory() != null) product.setCategory(request.getCategory());
        if (request.getStatus() != null) product.setStatus(request.getStatus());

        Product updatedProduct = productRepository.save(product);
        return ProductMapper.mapToDto(updatedProduct);
    }

    public void deleteProductById(String productId) {
        productRepository.deleteById(productId);
    }
}
