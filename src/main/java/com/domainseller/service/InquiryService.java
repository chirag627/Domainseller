package com.domainseller.service;

import com.domainseller.dto.InquiryDto;
import com.domainseller.exception.ResourceNotFoundException;
import com.domainseller.model.Inquiry;
import com.domainseller.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;

    public Inquiry createInquiry(InquiryDto dto) {
        Inquiry inquiry = Inquiry.builder()
                .domainId(dto.getDomainId())
                .domainName(dto.getDomainName())
                .senderName(dto.getSenderName())
                .senderEmail(dto.getSenderEmail())
                .message(dto.getMessage())
                .replied(false)
                .createdAt(LocalDateTime.now())
                .build();
        return inquiryRepository.save(inquiry);
    }

    public List<Inquiry> getInquiriesByDomain(String domainId) {
        return inquiryRepository.findByDomainIdOrderByCreatedAtDesc(domainId);
    }

    public List<Inquiry> getAllInquiries() {
        return inquiryRepository.findAllByOrderByCreatedAtDesc();
    }

    public void markReplied(String id) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inquiry not found: " + id));
        inquiry.setReplied(true);
        inquiryRepository.save(inquiry);
    }
}
