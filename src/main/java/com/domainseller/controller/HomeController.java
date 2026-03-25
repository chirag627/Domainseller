package com.domainseller.controller;

import com.domainseller.model.DomainCategory;
import com.domainseller.service.AuctionService;
import com.domainseller.service.DomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final DomainService domainService;
    private final AuctionService auctionService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("featuredDomains", domainService.getFeaturedDomains());
        model.addAttribute("auctionCount", auctionService.getAuctionCount());
        model.addAttribute("totalDomains", domainService.countAvailable());
        model.addAttribute("categories", DomainCategory.values());
        return "home";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }
}
