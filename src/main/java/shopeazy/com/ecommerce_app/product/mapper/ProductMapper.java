package shopeazy.com.ecommerce_app.product.mapper;

import org.springframework.stereotype.Component;
import shopeazy.com.ecommerce_app.product.dto.CreateProductRequest;
import shopeazy.com.ecommerce_app.product.dto.ProductResponseDto;
import shopeazy.com.ecommerce_app.product.model.Product;
import shopeazy.com.ecommerce_app.seller.dto.SellerDto;
import shopeazy.com.ecommerce_app.seller.mapper.SellerMapper;
import shopeazy.com.ecommerce_app.seller.model.Seller;

import java.time.Instant;


@Component
public class ProductMapper {

    public static ProductResponseDto mapToDto(Product product, Seller seller) {
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
        dto.setSeller(SellerMapper.toDto(seller));
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        return dto;
    }

    // Keep the old method for backward compatibility, but it will be deprecated
    @Deprecated
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
        dto.setSeller(null); // No seller data available
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