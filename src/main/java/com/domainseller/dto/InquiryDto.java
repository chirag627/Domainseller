package com.domainseller.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InquiryDto {
    @NotBlank(message = "Domain ID is required")
    private String domainId;
    private String domainName;
    @NotBlank(message = "Your name is required")
    private String senderName;
    @NotBlank(message = "Your email is required")
    @Email(message = "Invalid email format")
    private String senderEmail;
    @NotBlank(message = "Message is required")
    private String message;
}
