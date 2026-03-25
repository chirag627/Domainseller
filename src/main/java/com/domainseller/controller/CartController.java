package com.domainseller.controller;

import com.domainseller.dto.CheckoutDto;
import com.domainseller.model.Cart;
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
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final OrderService orderService;
    private final UserService userService;

    @GetMapping
    public String viewCart(Authentication auth, Model model) {
        String userId = userService.getCurrentUser(auth).getId();
        Cart cart = cartService.getOrCreateCart(userId);
        model.addAttribute("cart", cart);
        return "cart/cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam String domainId, Authentication auth,
                            RedirectAttributes redirectAttributes) {
        String userId = userService.getCurrentUser(auth).getId();
        cartService.addToCart(userId, domainId);
        redirectAttributes.addFlashAttribute("successMessage", "Domain added to cart!");
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam String domainId, Authentication auth,
                                  RedirectAttributes redirectAttributes) {
        String userId = userService.getCurrentUser(auth).getId();
        cartService.removeFromCart(userId, domainId);
        redirectAttributes.addFlashAttribute("successMessage", "Item removed from cart.");
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkoutForm(Authentication auth, Model model) {
        String userId = userService.getCurrentUser(auth).getId();
        Cart cart = cartService.getOrCreateCart(userId);
        model.addAttribute("cart", cart);
        model.addAttribute("checkoutDto", new CheckoutDto());
        return "cart/checkout";
    }

    @PostMapping("/checkout")
    public String processCheckout(@Valid @ModelAttribute("checkoutDto") CheckoutDto dto,
                                   BindingResult result, Authentication auth,
                                   Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            String userId = userService.getCurrentUser(auth).getId();
            model.addAttribute("cart", cartService.getOrCreateCart(userId));
            return "cart/checkout";
        }
        String userId = userService.getCurrentUser(auth).getId();
        try {
            orderService.createOrder(userId, dto);
            redirectAttributes.addFlashAttribute("successMessage", "Order placed successfully!");
            return "redirect:/orders";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/cart/checkout";
        }
    }
}
