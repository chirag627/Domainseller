package com.domainseller.service;

import com.domainseller.exception.ResourceNotFoundException;
import com.domainseller.model.Cart;
import com.domainseller.model.CartItem;
import com.domainseller.model.Domain;
import com.domainseller.repository.CartRepository;
import com.domainseller.repository.DomainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final DomainRepository domainRepository;

    public Cart getOrCreateCart(String userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart cart = Cart.builder()
                    .userId(userId)
                    .createdAt(LocalDateTime.now())
                    .build();
            return cartRepository.save(cart);
        });
    }

    public void addToCart(String userId, String domainId) {
        Domain domain = domainRepository.findById(domainId)
                .orElseThrow(() -> new ResourceNotFoundException("Domain not found"));
        Cart cart = getOrCreateCart(userId);
        boolean alreadyInCart = cart.getItems().stream()
                .anyMatch(item -> item.getDomainId().equals(domainId));
        if (!alreadyInCart) {
            cart.getItems().add(CartItem.builder()
                    .domainId(domain.getId())
                    .domainName(domain.getName() + domain.getExtension())
                    .price(domain.getPrice())
                    .addedAt(LocalDateTime.now())
                    .build());
            cartRepository.save(cart);
        }
    }

    public void removeFromCart(String userId, String domainId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().removeIf(item -> item.getDomainId().equals(domainId));
        cartRepository.save(cart);
    }

    public void clearCart(String userId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    public int getCartItemCount(String userId) {
        return cartRepository.findByUserId(userId)
                .map(c -> c.getItems().size())
                .orElse(0);
    }
}
