package com.domainseller.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document("watchlists")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Watchlist {
    @Id
    private String id;
    private String userId;
    @Builder.Default
    private List<String> domainIds = new ArrayList<>();
    private LocalDateTime updatedAt;
}
