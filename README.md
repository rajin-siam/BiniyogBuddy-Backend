# 📈 BiniyogBuddy

> "Invest in learning before money." — বিনিয়োগবাডি

A free, simple stock learning journal backend API for **beginner investors in Bangladesh**.  
Track DSE/CSE trades (real or paper), reflect on decisions, and build investing discipline.

---

## 🎯 What is BiniyogBuddy?

BiniyogBuddy helps new investors:
- **Learn** basic investing concepts
- **Track** paper or real trades in one place
- **Reflect** on decisions with journaling prompts
- **Understand** their own behavior and risk patterns

**Version:** 1.0 – Free MVP  
**Market:** Bangladesh (DSE / CSE)  
**Target User:** Beginner Investors

---

## ✨ Features (MVP)

| Feature | Description |
|---|---|
| 🔐 Auth | Sign up, login, logout with email + password |
| 👤 User Management | User profiles with experience level tracking |
| 📖 Stock Journal | Add stocks with DSE/CSE code, sector, price, quantity |
| 📝 Trade Logs | Log buy/sell entries — date, price, quantity |
| 💼 Portfolio View | See total invested amount and current holdings |
| 🧠 Trade Notes | "Why did I buy this?" and "What did I learn?" |
| 📚 REST API | Full RESTful API for frontend integration |
| 📘 API Documentation | OpenAPI/Swagger documentation |

---

## 🛠️ Tech Stack

- **Backend:** Spring Boot 4.0.3
- **Language:** Java 21
- **Database:** PostgreSQL
- **Authentication:** Spring Security with JWT
- **Build Tool:** Gradle
- **API Docs:** SpringDoc OpenAPI (Swagger)
- **Architecture:** Multi-module Gradle project

---

## 📁 Project Structure

```
biniyogbuddy/
├── apps/
│   └── api-app/              # Main Spring Boot application
│       └── src/main/
│           ├── java/         # API application code
│           └── resources/   # Configuration files
├── libs/
│   └── users/               # User module (entities, services, repositories)
│       └── src/
├── common/                   # Shared code (DTOs, exceptions, constants)
│   └── src/
├── build.gradle.kts         # Root build configuration
├── settings.gradle.kts      # Project settings
└── gradle.properties        # Gradle properties
```

---

## 🚀 Getting Started

### Prerequisites

- Java 21
- PostgreSQL (local or cloud)
- Gradle (or use gradlew wrapper)

### Database Setup

1. Create a PostgreSQL database:
```sql
CREATE DATABASE biniyogbuddy;
```

2. Update `apps/api-app/src/main/resources/application.yaml` with your database credentials.

### Installation

```bash
# 1. Clone the repository
git clone https://github.com/your-username/biniyogbuddy.git
cd biniyogbuddy

# 2. Build the project
./gradlew build

# 3. Run the application
./gradlew :apps:api-app:bootRun
```

The API will start on `http://localhost:8080`

---

## ⚙️ Configuration

### Application Properties

Located in `apps/api-app/src/main/resources/application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/biniyogbuddy
    username: your_username
    password: your_password
  jpa:
    hibernate:
      ddl-auto: update

server:
  port: 8080

springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
```

---
### Swagger Documentation

Access Swagger UI at: `http://localhost:8080/swagger-ui.html`

Access OpenAPI JSON at: `http://localhost:8080/v3/api-docs`

---

## 🗺️ Out of Scope (MVP v1)

- Live stock price feed (DSE/CSE API)
- Profit / Loss calculations
- Charts and analytics
- Social features
- Mobile app (iOS / Android native)
- Full Bangla language toggle
- Frontend application (separate project)

---

## 🤝 Contributing

This is an MVP built for learning purposes. Contributions, suggestions, and bug reports are welcome!  
Please open an issue before submitting a pull request.

---

## 📄 License

MIT License — free to use, modify, and distribute.

---

*Built with ❤️ for Bangladeshi beginner investors.*
