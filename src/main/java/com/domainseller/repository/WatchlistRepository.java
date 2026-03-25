package com.domainseller.repository;

import com.domainseller.model.Watchlist;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface WatchlistRepository extends MongoRepository<Watchlist, String> {
    Optional<Watchlist> findByUserId(String userId);
}
