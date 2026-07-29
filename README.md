# Vault

A simple banking web app built with Spring Boot for practice. It has an admin and a customer side — admins manage customers and cards, customers view their cards and transfer funds.

**Live:** https://vault-spring.onrender.com

## Built with

Java, Spring Boot, MySQL.

## Running locally

Needs JDK 21, Maven, and a MySQL database. Set these environment variables:

```
DB_URL=jdbc:mysql://localhost:3306/vault
DB_USER=your_user
DB_PASS=your_password
```

Then:

```bash
mvn spring-boot:run
```

App runs at http://localhost:8080.

## Docker

```bash
docker build -t vault .
docker run -p 8080:8080 -e DB_URL=... -e DB_USER=... -e DB_PASS=... vault
```

---

A learning project, not intended for production.