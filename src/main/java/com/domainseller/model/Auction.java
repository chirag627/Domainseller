package com.domainseller.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document("auctions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Auction {
    @Id
    private String id;
    private String domainId;
    private String domainName;
    private BigDecimal startingPrice;
    private BigDecimal currentBid;
    private String highestBidderId;
    private String highestBidderName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    @Builder.Default
    private boolean active = true;
    @Builder.Default
    private List<Bid> bids = new ArrayList<>();
}
