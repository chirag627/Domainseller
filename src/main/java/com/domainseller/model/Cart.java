package com.domainseller.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document("carts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    @Id
    private String id;
    private String userId;
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();
    private LocalDateTime createdAt;
}
