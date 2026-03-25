package com.domainseller.repository;

import com.domainseller.model.Domain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

public interface DomainRepository extends MongoRepository<Domain, String> {
    List<Domain> findByAvailableTrue();
    List<Domain> findByFeaturedTrueAndAvailableTrue();
    Page<Domain> findByAvailableTrue(Pageable pageable);

    @Query("{'available': true, '$or': [{'name': {$regex: ?0, $options: 'i'}}, {'description': {$regex: ?0, $options: 'i'}}, {'tags': {$regex: ?0, $options: 'i'}}]}")
    Page<Domain> searchDomains(String query, Pageable pageable);

    @Query("{'available': true, 'category': ?0}")
    Page<Domain> findByCategoryAndAvailableTrue(String category, Pageable pageable);

    long countByAvailableTrue();
    long countByFeaturedTrue();
}
