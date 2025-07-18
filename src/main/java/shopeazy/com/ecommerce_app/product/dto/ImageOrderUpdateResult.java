package shopeazy.com.ecommerce_app.product.dto;

import java.util.List;

public record ImageOrderUpdateResult(List<String> images, boolean updated) {}
