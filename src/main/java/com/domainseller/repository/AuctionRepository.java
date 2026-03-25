package com.domainseller.repository;

import com.domainseller.model.Auction;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface AuctionRepository extends MongoRepository<Auction, String> {
    List<Auction> findByActiveTrueOrderByEndTimeAsc();
    Optional<Auction> findByDomainIdAndActiveTrue(String domainId);
    long countByActiveTrue();
}
