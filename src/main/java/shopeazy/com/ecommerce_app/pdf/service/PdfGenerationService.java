package shopeazy.com.ecommerce_app.pdf.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import shopeazy.com.ecommerce_app.order.model.Order;
import shopeazy.com.ecommerce_app.order.model.OrderItem;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

/**
 * Service for generating PDF documents related to orders.
 * Uses iText library for PDF creation.
 */
@Service
@Slf4j
public class PdfGenerationService {
    
    /**
     * Generate invoice PDF for an order
     */
    public byte[] generateInvoicePdf(Order order) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);
            
            // Add invoice header
            addInvoiceHeader(document, order);
            
            // Add customer information
            addCustomerInformation(document, order);
            
            // Add order item table
            addOrderItemsTable(document, order);
            
            // Add pricing summary
            addPricingSummary(document, order);
            
            // Add footer
            addInvoiceFooter(document, order);
            
            document.close();
            
            log.info("Generated invoice PDF for order {}", order.getOrderNumber());
            return outputStream.toByteArray();
            
        } catch (Exception e) {
            log.error("Error generating invoice PDF for order {}: {}", order.getOrderNumber(), e.getMessage(), e);
            throw new RuntimeException("Failed to generate invoice PDF", e);
        }
    }
    
    /**
     * Generate order summary PDF
     */
    public byte[] generateOrderSummaryPdf(Order order) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);
            
            // Add header
            document.add(new Paragraph("ORDER SUMMARY")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20)
                    .setBold());
            
            document.add(new Paragraph("\n"));
            
            // Add order details
            addOrderDetails(document, order);
            
            // Add items
            addOrderItemsTable(document, order);
            
            // Add status information
            addOrderStatusInfo(document, order);
            
            document.close();
            
            log.info("Generated order summary PDF for order {}", order.getOrderNumber());
            return outputStream.toByteArray();
            
        } catch (Exception e) {
            log.error("Error generating order summary PDF for order {}: {}", order.getOrderNumber(), e.getMessage(), e);
            throw new RuntimeException("Failed to generate order summary PDF", e);
        }
    }
    
    /**
     * Generate cancellation receipt PDF
     */
    public byte[] generateCancellationReceiptPdf(Order order) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);
            
            // Add header
            document.add(new Paragraph("ORDER CANCELLATION RECEIPT")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(18)
                    .setBold());
            
            document.add(new Paragraph("\n"));
            
            // Add order details
            addOrderDetails(document, order);
            
            // Add cancellation information
            if (order.getCancellationInfo() != null) {
                document.add(new Paragraph("CANCELLATION DETAILS").setBold());
                document.add(new Paragraph("Reason: " + order.getCancellationInfo().getReason()));
                document.add(new Paragraph("Cancelled By: " + order.getCancellationInfo().getCancelledBy()));
                
                if (order.getCancellationInfo().getDetails() != null) {
                    document.add(new Paragraph("Details: " + order.getCancellationInfo().getDetails()));
                }
                
                if (order.getCancellationInfo().getRefundAmount() != null) {
                    document.add(new Paragraph("Refund Amount: $" + order.getCancellationInfo().getRefundAmount()));
                }
                
                document.add(new Paragraph("\n"));
            }
            
            // Add refund information
            document.add(new Paragraph("Refund will be processed within 3-5 business days to your original payment method."));
            
            document.close();
            
            log.info("Generated cancellation receipt PDF for order {}", order.getOrderNumber());
            return outputStream.toByteArray();
            
        } catch (Exception e) {
            log.error("Error generating cancellation receipt PDF for order {}: {}", order.getOrderNumber(), e.getMessage(), e);
            throw new RuntimeException("Failed to generate cancellation receipt PDF", e);
        }
    }
    
    // Helper methods for PDF generation
    
    private void addInvoiceHeader(Document document, Order order) {
        // Company header
        document.add(new Paragraph("SHOPEAZY")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(24)
                .setBold());
        
        document.add(new Paragraph("E-Commerce Platform")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(12));
        
        document.add(new Paragraph("\n"));
        
        // Invoice title and number
        document.add(new Paragraph("INVOICE")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(20)
                .setBold());
        
        document.add(new Paragraph("Invoice #: " + order.getOrderNumber())
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(14));
        
        if (order.getCreatedAt() != null) {
            document.add(new Paragraph("Date: " + 
                    order.getCreatedAt().atZone(java.time.ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("MMM dd, yyyy")))
                    .setTextAlignment(TextAlignment.CENTER));
        }
        
        document.add(new Paragraph("\n"));
    }
    
    private void addCustomerInformation(Document document, Order order) {
        document.add(new Paragraph("BILL TO:").setBold());
        document.add(new Paragraph(order.getCustomerName()));
        document.add(new Paragraph(order.getCustomerEmail()));
        
        if (order.getBillingAddress() != null) {
            document.add(new Paragraph(order.getBillingAddress().getStreet()));
            document.add(new Paragraph(order.getBillingAddress().getCity() + ", " +
                    order.getBillingAddress().getState() + " " + order.getBillingAddress().getZip()));
            document.add(new Paragraph(order.getBillingAddress().getCountry()));
        }
        
        document.add(new Paragraph("\n"));
        
        // Shipping address if different
        if (order.getShippingAddress() != null && !order.getShippingAddress().equals(order.getBillingAddress())) {
            document.add(new Paragraph("SHIP TO:").setBold());
            document.add(new Paragraph(order.getShippingAddress().getStreet()));
            document.add(new Paragraph(order.getShippingAddress().getCity() + ", " +
                    order.getShippingAddress().getState() + " " + order.getShippingAddress().getZip()));
            document.add(new Paragraph(order.getShippingAddress().getCountry()));
            document.add(new Paragraph("\n"));
        }
    }
    
    private void addOrderItemsTable(Document document, Order order) {
        // Create table with 4 columns
        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 1, 2, 2}));
        table.setWidth(UnitValue.createPercentValue(100));
        
        // Add headers
        table.addHeaderCell(new Cell().add(new Paragraph("Item").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Qty").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Unit Price").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Total").setBold()));
        
        // Add items
        for (OrderItem item : order.getOrderItems()) {
            table.addCell(new Cell().add(new Paragraph(item.getProductName())));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getQuantity()))));
            table.addCell(new Cell().add(new Paragraph("$" + item.getPriceAtTime())));
            table.addCell(new Cell().add(new Paragraph("$" + item.getTotalPrice())));
        }
        
        document.add(table);
        document.add(new Paragraph("\n"));
    }
    
    private void addPricingSummary(Document document, Order order) {
        if (order.getPricing() == null) return;
        
        // Create pricing summary table
        Table pricingTable = new Table(UnitValue.createPercentArray(new float[]{3, 1}));
        pricingTable.setWidth(UnitValue.createPercentValue(50));
        pricingTable.setMarginLeft(200);
        
        pricingTable.addCell(new Cell().add(new Paragraph("Subtotal:")));
        pricingTable.addCell(new Cell().add(new Paragraph("$" + order.getPricing().getSubtotal())));
        
        if (order.getPricing().getTax().compareTo(BigDecimal.ZERO) > 0) {
            pricingTable.addCell(new Cell().add(new Paragraph("Tax:")));
            pricingTable.addCell(new Cell().add(new Paragraph("$" + order.getPricing().getTax())));
        }
        
        if (order.getPricing().getShipping().compareTo(BigDecimal.ZERO) > 0) {
            pricingTable.addCell(new Cell().add(new Paragraph("Shipping:")));
            pricingTable.addCell(new Cell().add(new Paragraph("$" + order.getPricing().getShipping())));
        }
        
        if (order.getPricing().getDiscount().compareTo(BigDecimal.ZERO) > 0) {
            pricingTable.addCell(new Cell().add(new Paragraph("Discount:")));
            pricingTable.addCell(new Cell().add(new Paragraph("-$" + order.getPricing().getDiscount())));
        }
        
        // Total
        pricingTable.addCell(new Cell().add(new Paragraph("TOTAL:").setBold()));
        pricingTable.addCell(new Cell().add(new Paragraph("$" + order.getPricing().getTotal()).setBold()));
        
        document.add(pricingTable);
        document.add(new Paragraph("\n"));
    }
    
    private void addInvoiceFooter(Document document, Order order) {
        document.add(new Paragraph("Payment Status: " + order.getPaymentStatus()));
        document.add(new Paragraph("Order Status: " + order.getStatus()));
        
        if (order.getPaymentTransactionId() != null) {
            document.add(new Paragraph("Transaction ID: " + order.getPaymentTransactionId()));
        }
        
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("Thank you for your business!")
                .setTextAlignment(TextAlignment.CENTER)
                .setItalic());
    }
    
    private void addOrderDetails(Document document, Order order) {
        document.add(new Paragraph("ORDER DETAILS").setBold());
        document.add(new Paragraph("Order Number: " + order.getOrderNumber()));
        document.add(new Paragraph("Customer: " + order.getCustomerName()));
        document.add(new Paragraph("Email: " + order.getCustomerEmail()));
        
        if (order.getCreatedAt() != null) {
            document.add(new Paragraph("Order Date: " + 
                    order.getCreatedAt().atZone(java.time.ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))));
        }
        
        document.add(new Paragraph("Status: " + order.getStatus()));
        document.add(new Paragraph("Total: $" + 
                (order.getPricing() != null ? order.getPricing().getTotal() : "0.00")));
        
        document.add(new Paragraph("\n"));
    }
    
    private void addOrderStatusInfo(Document document, Order order) {
        document.add(new Paragraph("STATUS INFORMATION").setBold());
        document.add(new Paragraph("Current Status: " + order.getStatus()));
        document.add(new Paragraph("Payment Status: " + order.getPaymentStatus()));
        
        if (order.getTimestamps() != null) {
            if (order.getTimestamps().getCreated() != null) {
                document.add(new Paragraph("Created: " + 
                        order.getTimestamps().getCreated().atZone(java.time.ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))));
            }
            
            if (order.getTimestamps().getConfirmed() != null) {
                document.add(new Paragraph("Confirmed: " + 
                        order.getTimestamps().getConfirmed().atZone(java.time.ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))));
            }
            
            if (order.getTimestamps().getShipped() != null) {
                document.add(new Paragraph("Shipped: " + 
                        order.getTimestamps().getShipped().atZone(java.time.ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))));
            }
            
            if (order.getTimestamps().getDelivered() != null) {
                document.add(new Paragraph("Delivered: " + 
                        order.getTimestamps().getDelivered().atZone(java.time.ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))));
            }
        }
        
        // Tracking information
        if (order.getTrackingInfo() != null) {
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("TRACKING INFORMATION").setBold());
            
            if (order.getTrackingInfo().getTrackingNumber() != null) {
                document.add(new Paragraph("Tracking Number: " + order.getTrackingInfo().getTrackingNumber()));
            }
            
            if (order.getTrackingInfo().getCarrier() != null) {
                document.add(new Paragraph("Carrier: " + order.getTrackingInfo().getCarrier()));
            }
            
            if (order.getTrackingInfo().getEstimatedDelivery() != null) {
                document.add(new Paragraph("Estimated Delivery: " + 
                        order.getTrackingInfo().getEstimatedDelivery().atZone(java.time.ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))));
            }
        }
    }
}