package com.domainseller.model;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private String domainId;
    private String domainName;
    private BigDecimal price;
    private LocalDateTime addedAt;
}
