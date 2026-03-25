package com.domainseller.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutDto {
    @NotBlank(message = "Billing name is required")
    private String billingName;
    @NotBlank(message = "Billing email is required")
    @Email(message = "Invalid email format")
    private String billingEmail;
    @NotBlank(message = "Billing address is required")
    private String billingAddress;
    @NotBlank(message = "City is required")
    private String billingCity;
    @NotBlank(message = "Country is required")
    private String billingCountry;
}
