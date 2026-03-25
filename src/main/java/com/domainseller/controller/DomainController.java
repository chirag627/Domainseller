package com.domainseller.controller;

import com.domainseller.dto.InquiryDto;
import com.domainseller.model.Domain;
import com.domainseller.model.DomainCategory;
import com.domainseller.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;

@Controller
@RequestMapping("/domains")
@RequiredArgsConstructor
public class DomainController {

    private final DomainService domainService;
    private final WatchlistService watchlistService;
    private final InquiryService inquiryService;
    private final UserService userService;

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(required = false) String category,
                       @RequestParam(required = false) BigDecimal minPrice,
                       @RequestParam(required = false) BigDecimal maxPrice,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "12") int size,
                       @RequestParam(defaultValue = "createdAt,desc") String sort,
                       Model model) {
        String[] sortParts = sort.split(",");
        Sort.Direction direction = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParts[0]));
        Page<Domain> domains = domainService.searchDomains(q, category, minPrice, maxPrice, pageable);
        model.addAttribute("domains", domains);
        model.addAttribute("categories", DomainCategory.values());
        model.addAttribute("q", q);
        model.addAttribute("category", category);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("sort", sort);
        return "domains/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable String id, Model model, Authentication auth) {
        Domain domain = domainService.getDomainById(id);
        model.addAttribute("domain", domain);
        model.addAttribute("relatedDomains", domainService.getRelatedDomains(domain));
        model.addAttribute("inquiries", inquiryService.getInquiriesByDomain(id));
        model.addAttribute("inquiryDto", new InquiryDto(id, domain.getName() + domain.getExtension(), "", "", ""));
        if (auth != null && auth.isAuthenticated()) {
            model.addAttribute("isWatched", watchlistService.isWatched(
                    userService.getCurrentUser(auth).getId(), id));
        } else {
            model.addAttribute("isWatched", false);
        }
        return "domains/detail";
    }

    @PostMapping("/{id}/inquiry")
    public String submitInquiry(@PathVariable String id,
                                @Valid @ModelAttribute("inquiryDto") InquiryDto dto,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please fill in all required fields.");
            return "redirect:/domains/" + id;
        }
        inquiryService.createInquiry(dto);
        redirectAttributes.addFlashAttribute("successMessage", "Your inquiry has been sent!");
        return "redirect:/domains/" + id;
    }

    @PostMapping("/{id}/watchlist")
    public String toggleWatchlist(@PathVariable String id, Authentication auth,
                                   RedirectAttributes redirectAttributes) {
        String userId = userService.getCurrentUser(auth).getId();
        watchlistService.toggleWatchlist(userId, id);
        redirectAttributes.addFlashAttribute("successMessage", "Watchlist updated.");
        return "redirect:/domains/" + id;
    }
}
