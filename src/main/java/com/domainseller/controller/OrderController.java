package com.domainseller.controller;

import com.domainseller.exception.ResourceNotFoundException;
import com.domainseller.model.Order;
import com.domainseller.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @GetMapping
    public String listOrders(Authentication auth, Model model) {
        String userId = userService.getCurrentUser(auth).getId();
        model.addAttribute("orders", orderService.getOrdersByUser(userId));
        return "orders/list";
    }

    @GetMapping("/{id}")
    public String orderDetail(@PathVariable String id, Authentication auth, Model model) {
        Order order = orderService.getOrderById(id);
        String userId = userService.getCurrentUser(auth).getId();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!order.getUserId().equals(userId) && !isAdmin) {
            throw new ResourceNotFoundException("Order not found");
        }
        model.addAttribute("order", order);
        return "orders/detail";
    }
}
