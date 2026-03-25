package com.domainseller.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BidDto {
    @NotNull(message = "Bid amount is required")
    @DecimalMin(value = "0.01", message = "Bid must be at least 0.01")
    private BigDecimal amount;
}
