package shopeazy.com.ecommerceapp.service.serviceImplementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shopeazy.com.ecommerceapp.exceptions.ResourceNotFoundException;
import shopeazy.com.ecommerceapp.mapper.ProductMapper;
import shopeazy.com.ecommerceapp.model.document.Product;
import shopeazy.com.ecommerceapp.model.dto.response.ProductResponseDto;
import shopeazy.com.ecommerceapp.repository.ProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public ProductResponseDto getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product by product Id " + id + " doesn't exist"));
        return ProductMapper.mapToDto(product);
    }

}
