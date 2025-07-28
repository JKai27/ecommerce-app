package shopeazy.com.ecommerce_app.product.mapper;

import org.springframework.stereotype.Component;
import shopeazy.com.ecommerce_app.product.dto.ProductResponseDto;
import shopeazy.com.ecommerce_app.product.model.Product;
import shopeazy.com.ecommerce_app.seller.mapper.SellerMapper;
import shopeazy.com.ecommerce_app.seller.model.Seller;



@Component
public class ProductMapper {
    private ProductMapper() {
    }

    public static ProductResponseDto mapToDto(Product product, Seller seller) {
        if (product == null) return null;

        ProductResponseDto dto = new ProductResponseDto();
        dto.setId(product.getId());
        dto.setProductNumber(product.getProductNumber());
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
}