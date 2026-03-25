package com.domainseller.service;

import com.domainseller.dto.DomainDto;
import com.domainseller.exception.ResourceNotFoundException;
import com.domainseller.model.Domain;
import com.domainseller.model.PriceHistory;
import com.domainseller.repository.DomainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DomainService {

    private final DomainRepository domainRepository;

    public Page<Domain> getAllAvailableDomains(Pageable pageable) {
        return domainRepository.findByAvailableTrue(pageable);
    }

    public Page<Domain> searchDomains(String query, String category, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        Page<Domain> page;
        if (query != null && !query.isBlank()) {
            page = domainRepository.searchDomains(query, pageable);
        } else if (category != null && !category.isBlank()) {
            page = domainRepository.findByCategoryAndAvailableTrue(category, pageable);
        } else {
            page = domainRepository.findByAvailableTrue(pageable);
        }
        if (minPrice != null || maxPrice != null) {
            List<Domain> filtered = page.getContent().stream()
                    .filter(d -> (minPrice == null || d.getPrice().compareTo(minPrice) >= 0)
                            && (maxPrice == null || d.getPrice().compareTo(maxPrice) <= 0))
                    .collect(Collectors.toList());
            return new PageImpl<>(filtered, pageable, filtered.size());
        }
        return page;
    }

    public Domain getDomainById(String id) {
        Domain domain = domainRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Domain not found: " + id));
        domain.setViews(domain.getViews() + 1);
        domainRepository.save(domain);
        return domain;
    }

    public Domain createDomain(DomainDto dto) {
        List<String> tags = parseTags(dto.getTags());
        Domain domain = Domain.builder()
                .name(dto.getName())
                .extension(dto.getExtension())
                .category(dto.getCategory())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .available(true)
                .featured(dto.isFeatured())
                .tags(tags)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        domain.getPriceHistory().add(PriceHistory.builder()
                .price(dto.getPrice())
                .changedAt(LocalDateTime.now())
                .note("Initial price")
                .build());
        return domainRepository.save(domain);
    }

    public Domain updateDomain(String id, DomainDto dto) {
        Domain domain = domainRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Domain not found: " + id));
        if (domain.getPrice().compareTo(dto.getPrice()) != 0) {
            domain.getPriceHistory().add(PriceHistory.builder()
                    .price(dto.getPrice())
                    .changedAt(LocalDateTime.now())
                    .note("Price updated")
                    .build());
        }
        domain.setName(dto.getName());
        domain.setExtension(dto.getExtension());
        domain.setCategory(dto.getCategory());
        domain.setDescription(dto.getDescription());
        domain.setPrice(dto.getPrice());
        domain.setFeatured(dto.isFeatured());
        domain.setTags(parseTags(dto.getTags()));
        domain.setUpdatedAt(LocalDateTime.now());
        return domainRepository.save(domain);
    }

    public void deleteDomain(String id) {
        domainRepository.deleteById(id);
    }

    public void toggleAvailability(String id) {
        Domain domain = domainRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Domain not found: " + id));
        domain.setAvailable(!domain.isAvailable());
        domainRepository.save(domain);
    }

    public List<Domain> getFeaturedDomains() {
        return domainRepository.findByFeaturedTrueAndAvailableTrue();
    }

    public List<Domain> getRelatedDomains(Domain domain) {
        return domainRepository.findByAvailableTrue().stream()
                .filter(d -> d.getCategory().equals(domain.getCategory()) && !d.getId().equals(domain.getId()))
                .limit(4)
                .collect(Collectors.toList());
    }

    public List<Domain> getAllDomains() {
        return domainRepository.findAll();
    }

    public long countAvailable() {
        return domainRepository.countByAvailableTrue();
    }

    public long countAll() {
        return domainRepository.count();
    }

    private List<String> parseTags(String tags) {
        if (tags == null || tags.isBlank()) return List.of();
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
