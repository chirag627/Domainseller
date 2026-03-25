package com.domainseller.model;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bid {
    private String bidderId;
    private String bidderName;
    private BigDecimal amount;
    private LocalDateTime placedAt;
}
