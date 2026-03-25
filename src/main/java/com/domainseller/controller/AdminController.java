package com.domainseller.controller;

import com.domainseller.dto.DomainDto;
import com.domainseller.model.*;
import com.domainseller.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final DomainService domainService;
    private final UserService userService;
    private final OrderService orderService;
    private final InquiryService inquiryService;
    private final AuctionService auctionService;

    @GetMapping
    public String dashboard(Model model) {
        List<Order> allOrders = orderService.getAllOrders();
        List<Order> recentOrders = allOrders.stream().limit(5).toList();
        model.addAttribute("totalDomains", domainService.countAll());
        model.addAttribute("availableDomains", domainService.countAvailable());
        model.addAttribute("totalUsers", userService.getAllUsers().size());
        model.addAttribute("totalOrders", allOrders.size());
        model.addAttribute("revenue", orderService.calculateRevenue());
        model.addAttribute("auctionCount", auctionService.getAuctionCount());
        model.addAttribute("recentOrders", recentOrders);
        model.addAttribute("recentInquiries", inquiryService.getAllInquiries().stream().limit(5).toList());
        return "admin/dashboard";
    }

    // Domain CRUD
    @GetMapping("/domains")
    public String domains(Model model) {
        model.addAttribute("domains", domainService.getAllDomains());
        return "admin/domains/list";
    }

    @GetMapping("/domains/new")
    public String newDomainForm(Model model) {
        model.addAttribute("domainDto", new DomainDto());
        model.addAttribute("categories", DomainCategory.values());
        model.addAttribute("isEdit", false);
        return "admin/domains/form";
    }

    @PostMapping("/domains")
    public String createDomain(@Valid @ModelAttribute("domainDto") DomainDto dto,
                                BindingResult result, Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categories", DomainCategory.values());
            model.addAttribute("isEdit", false);
            return "admin/domains/form";
        }
        domainService.createDomain(dto);
        redirectAttributes.addFlashAttribute("successMessage", "Domain created successfully!");
        return "redirect:/admin/domains";
    }

    @GetMapping("/domains/{id}/edit")
    public String editDomainForm(@PathVariable String id, Model model) {
        Domain domain = domainService.getDomainById(id);
        DomainDto dto = new DomainDto();
        dto.setName(domain.getName());
        dto.setExtension(domain.getExtension());
        dto.setCategory(domain.getCategory());
        dto.setDescription(domain.getDescription());
        dto.setPrice(domain.getPrice());
        dto.setFeatured(domain.isFeatured());
        dto.setTags(String.join(", ", domain.getTags()));
        model.addAttribute("domainDto", dto);
        model.addAttribute("domainId", id);
        model.addAttribute("categories", DomainCategory.values());
        model.addAttribute("isEdit", true);
        return "admin/domains/form";
    }

    @PostMapping("/domains/{id}")
    public String updateDomain(@PathVariable String id,
                                @Valid @ModelAttribute("domainDto") DomainDto dto,
                                BindingResult result, Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categories", DomainCategory.values());
            model.addAttribute("domainId", id);
            model.addAttribute("isEdit", true);
            return "admin/domains/form";
        }
        domainService.updateDomain(id, dto);
        redirectAttributes.addFlashAttribute("successMessage", "Domain updated successfully!");
        return "redirect:/admin/domains";
    }

    @PostMapping("/domains/{id}/delete")
    public String deleteDomain(@PathVariable String id, RedirectAttributes redirectAttributes) {
        domainService.deleteDomain(id);
        redirectAttributes.addFlashAttribute("successMessage", "Domain deleted.");
        return "redirect:/admin/domains";
    }

    @PostMapping("/domains/{id}/toggle")
    public String toggleDomain(@PathVariable String id, RedirectAttributes redirectAttributes) {
        domainService.toggleAvailability(id);
        redirectAttributes.addFlashAttribute("successMessage", "Domain availability toggled.");
        return "redirect:/admin/domains";
    }

    // Auctions
    @GetMapping("/auctions")
    public String auctions(Model model) {
        model.addAttribute("auctions", auctionService.getActiveAuctions());
        model.addAttribute("domains", domainService.getAllDomains());
        return "admin/auctions/list";
    }

    @GetMapping("/auctions/new")
    public String newAuctionForm(Model model) {
        model.addAttribute("domains", domainService.getAllDomains());
        return "admin/auctions/form";
    }

    @PostMapping("/auctions")
    public String createAuction(@RequestParam String domainId,
                                 @RequestParam BigDecimal startingPrice,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
                                 RedirectAttributes redirectAttributes) {
        auctionService.createAuction(domainId, startingPrice, endTime);
        redirectAttributes.addFlashAttribute("successMessage", "Auction created!");
        return "redirect:/admin/auctions";
    }

    @PostMapping("/auctions/{id}/end")
    public String endAuction(@PathVariable String id, RedirectAttributes redirectAttributes) {
        auctionService.endAuction(id);
        redirectAttributes.addFlashAttribute("successMessage", "Auction ended.");
        return "redirect:/admin/auctions";
    }

    // Orders
    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        model.addAttribute("statuses", OrderStatus.values());
        return "admin/orders/list";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable String id,
                                     @RequestParam String status,
                                     RedirectAttributes redirectAttributes) {
        orderService.updateOrderStatus(id, status);
        redirectAttributes.addFlashAttribute("successMessage", "Order status updated.");
        return "redirect:/admin/orders";
    }

    // Inquiries
    @GetMapping("/inquiries")
    public String inquiries(Model model) {
        model.addAttribute("inquiries", inquiryService.getAllInquiries());
        return "admin/inquiries/list";
    }

    @PostMapping("/inquiries/{id}/mark-replied")
    public String markReplied(@PathVariable String id, RedirectAttributes redirectAttributes) {
        inquiryService.markReplied(id);
        redirectAttributes.addFlashAttribute("successMessage", "Marked as replied.");
        return "redirect:/admin/inquiries";
    }

    // Users
    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users/list";
    }
}
