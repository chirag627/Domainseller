package com.domainseller.controller;

import com.domainseller.dto.BidDto;
import com.domainseller.model.User;
import com.domainseller.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auctions")
@RequiredArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;
    private final UserService userService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("auctions", auctionService.getActiveAuctions());
        return "auctions/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable String id, Model model) {
        model.addAttribute("auction", auctionService.getAuctionById(id));
        model.addAttribute("bidDto", new BidDto());
        return "auctions/detail";
    }

    @PostMapping("/{id}/bid")
    public String placeBid(@PathVariable String id,
                           @Valid @ModelAttribute("bidDto") BidDto dto,
                           BindingResult result,
                           Authentication auth,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid bid amount.");
            return "redirect:/auctions/" + id;
        }
        User user = userService.getCurrentUser(auth);
        try {
            auctionService.placeBid(id, user.getId(), user.getFullName(), dto.getAmount());
            redirectAttributes.addFlashAttribute("successMessage", "Bid placed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/auctions/" + id;
    }
}
