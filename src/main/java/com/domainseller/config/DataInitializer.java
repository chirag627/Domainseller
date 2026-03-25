package com.domainseller.config;

import com.domainseller.model.*;
import com.domainseller.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DomainRepository domainRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("admin")) {
            userRepository.save(User.builder()
                    .username("admin")
                    .email("admin@domainseller.com")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("Administrator")
                    .role("ROLE_ADMIN")
                    .createdAt(LocalDateTime.now())
                    .active(true)
                    .build());
        }
        if (!userRepository.existsByUsername("user")) {
            userRepository.save(User.builder()
                    .username("user")
                    .email("user@domainseller.com")
                    .password(passwordEncoder.encode("user123"))
                    .fullName("Test User")
                    .role("ROLE_USER")
                    .createdAt(LocalDateTime.now())
                    .active(true)
                    .build());
        }
        if (domainRepository.count() == 0) {
            List<Domain> domains = Arrays.asList(
                createDomain("tech", ".io", "TECH", "Premium tech domain for startups and SaaS companies", new BigDecimal("4999"), true, Arrays.asList("tech", "startup", "saas"), true),
                createDomain("cloudbase", ".net", "TECH", "Cloud infrastructure and hosting services domain", new BigDecimal("2500"), false, Arrays.asList("cloud", "hosting", "infrastructure"), false),
                createDomain("aitools", ".dev", "TECH", "AI tools and machine learning platform domain", new BigDecimal("8999"), true, Arrays.asList("ai", "machinelearning", "tools"), true),
                createDomain("securevault", ".com", "TECH", "Cybersecurity and data protection platform", new BigDecimal("15000"), false, Arrays.asList("security", "vault", "protection"), true),
                createDomain("datastream", ".io", "TECH", "Real-time data streaming and analytics platform", new BigDecimal("3200"), false, Arrays.asList("data", "streaming", "analytics"), false),
                createDomain("tradepro", ".finance", "FINANCE", "Professional trading and investment platform", new BigDecimal("5500"), true, Arrays.asList("trading", "investment", "finance"), true),
                createDomain("cryptoledger", ".io", "FINANCE", "Cryptocurrency ledger and blockchain platform", new BigDecimal("7800"), false, Arrays.asList("crypto", "blockchain", "ledger"), false),
                createDomain("investwise", ".net", "FINANCE", "Smart investment advisory and portfolio management", new BigDecimal("1200"), false, Arrays.asList("invest", "portfolio", "advisory"), false),
                createDomain("healthtrack", ".com", "HEALTH", "Health monitoring and wellness tracking platform", new BigDecimal("6500"), true, Arrays.asList("health", "wellness", "tracking"), true),
                createDomain("medportal", ".health", "HEALTH", "Medical portal and patient management system", new BigDecimal("3800"), false, Arrays.asList("medical", "portal", "patient"), false),
                createDomain("shopnow", ".store", "E_COMMERCE", "E-commerce and retail platform domain", new BigDecimal("2200"), true, Arrays.asList("shop", "ecommerce", "retail"), false),
                createDomain("ecomhub", ".net", "E_COMMERCE", "E-commerce hub for online merchants", new BigDecimal("4100"), false, Arrays.asList("ecommerce", "hub", "merchants"), false),
                createDomain("learnfast", ".edu", "EDUCATION", "Fast-paced e-learning and education platform", new BigDecimal("1800"), true, Arrays.asList("learning", "education", "courses"), false),
                createDomain("skillup", ".academy", "EDUCATION", "Professional skills and career development", new BigDecimal("2900"), false, Arrays.asList("skills", "career", "development"), false),
                createDomain("travelmate", ".com", "TRAVEL", "Travel companion and trip planning platform", new BigDecimal("5200"), true, Arrays.asList("travel", "trip", "planning"), true),
                createDomain("wanderlust", ".travel", "TRAVEL", "Wanderlust travel community and booking platform", new BigDecimal("3600"), false, Arrays.asList("wanderlust", "travel", "community"), false),
                createDomain("designstudio", ".io", "DESIGN", "Creative design studio and agency platform", new BigDecimal("4400"), true, Arrays.asList("design", "creative", "studio"), false),
                createDomain("pixelcraft", ".design", "DESIGN", "Pixel-perfect design tools and resources", new BigDecimal("2800"), false, Arrays.asList("pixel", "design", "tools"), false),
                createDomain("gamenation", ".gg", "GAMING", "Gaming community and esports platform", new BigDecimal("6200"), true, Arrays.asList("gaming", "esports", "community"), true),
                createDomain("sportzzone", ".com", "SPORTS", "Sports news, scores, and community hub", new BigDecimal("3100"), false, Arrays.asList("sports", "news", "scores"), false)
            );
            domainRepository.saveAll(domains);
        }
    }

    private Domain createDomain(String name, String extension, String category,
                                  String description, BigDecimal price, boolean available,
                                  List<String> tags, boolean featured) {
        Domain domain = Domain.builder()
                .name(name)
                .extension(extension)
                .category(category)
                .description(description)
                .price(price)
                .available(available)
                .featured(featured)
                .tags(tags)
                .views(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        domain.getPriceHistory().add(PriceHistory.builder()
                .price(price)
                .changedAt(LocalDateTime.now())
                .note("Initial price")
                .build());
        return domain;
    }
}
