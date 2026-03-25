package com.domainseller.service;

import com.domainseller.dto.CheckoutDto;
import com.domainseller.exception.ResourceNotFoundException;
import com.domainseller.model.*;
import com.domainseller.repository.DomainRepository;
import com.domainseller.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final UserService userService;
    private final DomainRepository domainRepository;

    public Order createOrder(String userId, CheckoutDto dto) {
        User user = userService.findById(userId);
        Cart cart = cartService.getOrCreateCart(userId);
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }
        BigDecimal total = cart.getItems().stream()
                .map(CartItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Order order = Order.builder()
                .userId(userId)
                .userEmail(user.getEmail())
                .userName(user.getFullName())
                .items(List.copyOf(cart.getItems()))
                .totalAmount(total)
                .status(OrderStatus.PENDING)
                .billingName(dto.getBillingName())
                .billingEmail(dto.getBillingEmail())
                .billingAddress(dto.getBillingAddress())
                .billingCity(dto.getBillingCity())
                .billingCountry(dto.getBillingCountry())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Order saved = orderRepository.save(order);
        cart.getItems().forEach(item -> {
            domainRepository.findById(item.getDomainId()).ifPresent(d -> {
                d.setAvailable(false);
                domainRepository.save(d);
            });
        });
        cartService.clearCart(userId);
        return saved;
    }

    public List<Order> getOrdersByUser(String userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Order getOrderById(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    public void updateOrderStatus(String id, String status) {
        Order order = getOrderById(id);
        order.setStatus(OrderStatus.valueOf(status));
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    public BigDecimal calculateRevenue() {
        return getAllOrders().stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
