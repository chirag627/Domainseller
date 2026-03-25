# DomainSeller Pro

Advanced domain marketplace platform built with Spring Boot 3, MongoDB, and Thymeleaf.

## Features
- Domain Marketplace with search, filter, sort, pagination
- Auction system with real-time countdown
- Shopping cart and checkout
- User authentication and profiles
- Admin dashboard with full CRUD
- Watchlist/favorites
- Inquiry system

## Tech Stack
- Spring Boot 3.2.5 / Java 17
- MongoDB
- Thymeleaf + Bootstrap 5.3
- Spring Security 6

## Quick Start with Docker
```bash
docker-compose up --build
```
Access at http://localhost:8080

## Local Development
```bash
# Start MongoDB first
docker run -d -p 27017:27017 mongo:7.0

# Run application
mvn spring-boot:run
```

## Default Credentials
- Admin: admin / admin123
- User: user / user123

## MongoDB Connection
Default: mongodb://localhost:27017/domainseller
