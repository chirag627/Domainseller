package com.domainseller.repository;

import com.domainseller.model.Inquiry;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface InquiryRepository extends MongoRepository<Inquiry, String> {
    List<Inquiry> findByDomainIdOrderByCreatedAtDesc(String domainId);
    List<Inquiry> findAllByOrderByCreatedAtDesc();
}
