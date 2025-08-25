package shopeazy.com.ecommerce_app.events.handler;

public record ProductPriceChangedEvent(String productNumber, Double price) {
}
