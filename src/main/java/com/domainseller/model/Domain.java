package com.domainseller.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document("domains")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Domain {
    @Id
    private String id;
    private String name;
    private String extension;
    private String category;
    private String description;
    private BigDecimal price;
    @Builder.Default
    private boolean available = true;
    private boolean featured;
    @Builder.Default
    private int views = 0;
    @Builder.Default
    private List<String> tags = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Builder.Default
    private List<PriceHistory> priceHistory = new ArrayList<>();
}
