package com.domainseller.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document("inquiries")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inquiry {
    @Id
    private String id;
    private String domainId;
    private String domainName;
    private String senderName;
    private String senderEmail;
    private String message;
    @Builder.Default
    private boolean replied = false;
    private LocalDateTime createdAt;
}
