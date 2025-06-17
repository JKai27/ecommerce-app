package shopeazy.com.ecommerceapp.mapper;

import org.springframework.stereotype.Component;
import shopeazy.com.ecommerceapp.model.dto.request.ProductDto;
import shopeazy.com.ecommerceapp.model.dto.response.ProductResponseDto;
import shopeazy.com.ecommerceapp.model.document.Product;


@Component
public class ProductMapper {

    public static ProductResponseDto toDto(Product product) {
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

    public static Product toEntity(ProductDto dto) {
        return null;
    }
}