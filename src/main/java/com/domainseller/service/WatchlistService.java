package com.domainseller.service;

import com.domainseller.model.Domain;
import com.domainseller.model.Watchlist;
import com.domainseller.repository.DomainRepository;
import com.domainseller.repository.WatchlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final DomainRepository domainRepository;

    public Watchlist getWatchlist(String userId) {
        return watchlistRepository.findByUserId(userId).orElseGet(() ->
                Watchlist.builder().userId(userId).updatedAt(LocalDateTime.now()).build());
    }

    public void toggleWatchlist(String userId, String domainId) {
        Watchlist watchlist = watchlistRepository.findByUserId(userId).orElseGet(() ->
                Watchlist.builder().userId(userId).updatedAt(LocalDateTime.now()).build());
        if (watchlist.getDomainIds().contains(domainId)) {
            watchlist.getDomainIds().remove(domainId);
        } else {
            watchlist.getDomainIds().add(domainId);
        }
        watchlist.setUpdatedAt(LocalDateTime.now());
        watchlistRepository.save(watchlist);
    }

    public boolean isWatched(String userId, String domainId) {
        return watchlistRepository.findByUserId(userId)
                .map(w -> w.getDomainIds().contains(domainId))
                .orElse(false);
    }

    public List<Domain> getWatchedDomains(String userId) {
        Watchlist watchlist = watchlistRepository.findByUserId(userId).orElse(null);
        if (watchlist == null || watchlist.getDomainIds().isEmpty()) return List.of();
        return watchlist.getDomainIds().stream()
                .map(id -> domainRepository.findById(id).orElse(null))
                .filter(d -> d != null)
                .collect(Collectors.toList());
    }
}
