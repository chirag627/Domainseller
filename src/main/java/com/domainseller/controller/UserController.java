package com.domainseller.controller;

import com.domainseller.model.User;
import com.domainseller.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final WatchlistService watchlistService;
    private final OrderService orderService;

    @GetMapping("/profile")
    public String profile(Authentication auth, Model model) {
        User user = userService.getCurrentUser(auth);
        model.addAttribute("user", user);
        model.addAttribute("orders", orderService.getOrdersByUser(user.getId()));
        return "user/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam String fullName,
                                 @RequestParam String email,
                                 @RequestParam(required = false) String newPassword,
                                 Authentication auth,
                                 RedirectAttributes redirectAttributes) {
        User user = userService.getCurrentUser(auth);
        try {
            userService.updateProfile(user.getId(), fullName, email, newPassword);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update profile: " + e.getMessage());
        }
        return "redirect:/user/profile";
    }

    @GetMapping("/watchlist")
    public String watchlist(Authentication auth, Model model) {
        User user = userService.getCurrentUser(auth);
        model.addAttribute("watchedDomains", watchlistService.getWatchedDomains(user.getId()));
        return "user/watchlist";
    }
}
