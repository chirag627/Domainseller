package com.domainseller.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Document("orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    private String id;
    private String userId;
    private String userEmail;
    private String userName;
    private List<CartItem> items;
    private BigDecimal totalAmount;
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;
    private String billingName;
    private String billingEmail;
    private String billingAddress;
    private String billingCity;
    private String billingCountry;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
