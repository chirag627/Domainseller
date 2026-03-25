package com.domainseller.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DomainDto {
    @NotBlank(message = "Domain name is required")
    private String name;
    @NotBlank(message = "Extension is required")
    private String extension;
    @NotBlank(message = "Category is required")
    private String category;
    private String description;
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be at least 0.01")
    private BigDecimal price;
    private boolean featured;
    private String tags;
}
