package com.domainseller.service;

import com.domainseller.exception.ResourceNotFoundException;
import com.domainseller.model.Auction;
import com.domainseller.model.Bid;
import com.domainseller.model.Domain;
import com.domainseller.repository.AuctionRepository;
import com.domainseller.repository.DomainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final DomainRepository domainRepository;

    public List<Auction> getActiveAuctions() {
        List<Auction> auctions = auctionRepository.findByActiveTrueOrderByEndTimeAsc();
        auctions.forEach(a -> {
            if (a.getEndTime().isBefore(LocalDateTime.now())) {
                a.setActive(false);
                auctionRepository.save(a);
            }
        });
        return auctionRepository.findByActiveTrueOrderByEndTimeAsc();
    }

    public Auction getAuctionById(String id) {
        return auctionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Auction not found: " + id));
    }

    public Auction getAuctionByDomainId(String domainId) {
        return auctionRepository.findByDomainIdAndActiveTrue(domainId).orElse(null);
    }

    public Auction createAuction(String domainId, BigDecimal startingPrice, LocalDateTime endTime) {
        Domain domain = domainRepository.findById(domainId)
                .orElseThrow(() -> new ResourceNotFoundException("Domain not found: " + domainId));
        Auction auction = Auction.builder()
                .domainId(domainId)
                .domainName(domain.getName() + domain.getExtension())
                .startingPrice(startingPrice)
                .currentBid(startingPrice)
                .startTime(LocalDateTime.now())
                .endTime(endTime)
                .active(true)
                .build();
        return auctionRepository.save(auction);
    }

    public void placeBid(String auctionId, String bidderId, String bidderName, BigDecimal amount) {
        Auction auction = getAuctionById(auctionId);
        if (!auction.isActive()) {
            throw new IllegalStateException("Auction is not active");
        }
        if (auction.getEndTime().isBefore(LocalDateTime.now())) {
            auction.setActive(false);
            auctionRepository.save(auction);
            throw new IllegalStateException("Auction has ended");
        }
        if (auction.getCurrentBid() != null && amount.compareTo(auction.getCurrentBid()) <= 0) {
            throw new IllegalArgumentException("Bid must be higher than current bid of $" + auction.getCurrentBid());
        }
        Bid bid = Bid.builder()
                .bidderId(bidderId)
                .bidderName(bidderName)
                .amount(amount)
                .placedAt(LocalDateTime.now())
                .build();
        auction.getBids().add(bid);
        auction.setCurrentBid(amount);
        auction.setHighestBidderId(bidderId);
        auction.setHighestBidderName(bidderName);
        auctionRepository.save(auction);
    }

    public void endAuction(String id) {
        Auction auction = getAuctionById(id);
        auction.setActive(false);
        auctionRepository.save(auction);
    }

    public long getAuctionCount() {
        return auctionRepository.countByActiveTrue();
    }
}
