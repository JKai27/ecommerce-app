package shopeazy.com.ecommerce_app.seller.mapper;

import org.springframework.stereotype.Component;
import shopeazy.com.ecommerce_app.seller.dto.SellerDto;
import shopeazy.com.ecommerce_app.seller.model.Seller;

@Component
public class SellerMapper {

    public static SellerDto toDto(Seller seller) {
        if (seller == null) return null;

        SellerDto dto = new SellerDto();
        dto.setSellerId(seller.getSellerId());
        dto.setSellerNumber(seller.getSellerNumber());
        dto.setCompanyName(seller.getCompanyName());
        dto.setContactEmail(seller.getContactEmail());
        dto.setProductCount(seller.getProductCount());
        dto.setRegisteredAt(seller.getRegisteredAt());
        dto.setSellerStatus(seller.getSellerStatus());
        return dto;
    }
}