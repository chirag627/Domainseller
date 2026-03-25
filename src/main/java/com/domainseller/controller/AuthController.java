package com.domainseller.controller;

import com.domainseller.dto.UserRegistrationDto;
import com.domainseller.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("userRegistrationDto", new UserRegistrationDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("userRegistrationDto") UserRegistrationDto dto,
                           BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "auth/register";
        }
        try {
            userService.registerUser(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please login.");
            return "redirect:/auth/login";
        } catch (IllegalArgumentException e) {
            result.rejectValue(null, "error.dto", e.getMessage());
            return "auth/register";
        }
    }
}
