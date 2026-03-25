package com.domainseller.model;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceHistory {
    private BigDecimal price;
    private LocalDateTime changedAt;
    private String note;
}
