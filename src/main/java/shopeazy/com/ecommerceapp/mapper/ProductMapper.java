package shopeazy.com.ecommerceapp.mapper;

import org.springframework.stereotype.Component;
import shopeazy.com.ecommerceapp.enums.ProductStatus;
import shopeazy.com.ecommerceapp.model.dto.request.CreateProductRequest;
import shopeazy.com.ecommerceapp.model.dto.response.ProductResponseDto;
import shopeazy.com.ecommerceapp.model.document.Product;

import java.time.Instant;


@Component
public class ProductMapper {

    public static ProductResponseDto mapToDto(Product product) {
        if (product == null) return null;

        ProductResponseDto dto = new ProductResponseDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setDiscount(product.getDiscount());
        dto.setStockCount(product.getStockCount());
        dto.setCategory(product.getCategory());
        dto.setImages(product.getImages());
        dto.setStatus(product.getStatus().name());
        dto.setSellerId(product.getSellerId());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        return dto;
    }

    public static Product toEntity(CreateProductRequest dto) {
        if (dto == null) return null;

        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setDiscount(dto.getDiscount());
        product.setStockCount(dto.getStockCount());
        product.setCategory(dto.getCategory());
        product.setImages(dto.getImages());
        product.setSellerId(dto.getSellerId());
        product.setStatus(dto.getStatus());
        product.setCreatedAt(Instant.now());
        product.setUpdatedAt(Instant.now());
        return product;
    }
}