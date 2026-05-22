# QuizCloud

<div align="center">

![Java 17](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.14-6DB33F?style=flat-square&logo=spring-boot)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.0.0-6DB33F?style=flat-square&logo=spring)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14+-336791?style=flat-square&logo=postgresql)
![Maven](https://img.shields.io/badge/Maven-3.8+-C71A36?style=flat-square&logo=apache-maven)
![License](https://img.shields.io/badge/License-MIT-green?style=flat-square)

**Enterprise-grade Microservices Platform for Quiz Management**

A scalable, cloud-native microservices architecture built with Spring Boot and Spring Cloud. Designed for high availability, performance, and maintainability.

</div>

---

## 📑 Quick Navigation

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running Services](#running-services)
- [API Reference](#api-reference)
- [Development](#development)
- [Troubleshooting](#troubleshooting)

---

## 🎯 Overview

**QuizCloud** is a production-ready microservices platform that demonstrates enterprise architecture patterns. It provides a complete ecosystem for managing questions, creating quizzes, and delivering interactive assessments at scale.

### Key Use Cases
- Online learning and certification platforms
- Corporate training and skill assessments
- Competitive examination systems
- Educational technology solutions
- Real-time assessment delivery

---

## ✨ Features

| Feature | Description |
|---------|-------------|
| 🎓 **Question Management** | Full CRUD operations with categorization & difficulty levels |
| 📝 **Quiz Operations** | Create, manage, and execute quizzes with real-time delivery |
| 🔄 **Service Discovery** | Automatic registration & discovery via Netflix Eureka |
| 🚪 **API Gateway** | Single entry point with intelligent routing & load balancing |
| 🔗 **Inter-Service Communication** | Declarative HTTP clients with OpenFeign |
| 📊 **Advanced Filtering** | Filter questions by category and difficulty |
| 🗄️ **Data Persistence** | PostgreSQL integration with JPA/Hibernate |
| 🏗️ **Cloud-Native** | Container-ready with Kubernetes deployment support |
| ⚡ **High Availability** | Distributed architecture with fault tolerance |
| 📈 **Scalability** | Horizontal scaling for all services |

---

## 🏗️ Architecture

### System Design

```
┌─────────────────────────────────────────────────────────────┐
│                      Client Layer                            │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
                 ┌─────────────────────┐
                 │   API Gateway       │
                 │  (Spring Gateway)   │
                 │    Port 8765        │
                 └─────────┬───────────┘
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
        ▼                  ▼                  ▼
  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
  │ Question Svc │  │  Quiz Svc    │  │Service Regist│
  │  Port 8081   │  │  Port 8090   │  │  Port 8761   │
  └──────┬───────┘  └──────┬───────┘  └──────────────┘
         │                 │
         │         ┌───────┴────────┐
         │         │  OpenFeign     │
         │         │  HTTP Client   │
         │         └────────────────┘
         │
    ┌────┴──────────────┐
    │                   │
    ▼                   ▼
┌──────────────┐  ┌──────────────┐
│  PostgreSQL  │  │  PostgreSQL  │
│ questiondb   │  │   quizdb     │
└──────────────┘  └──────────────┘
```

### Service Details

| Service | Port | Purpose | Database |
|---------|------|---------|----------|
| Service Registry | 8761 | Eureka - Service Discovery | — |
| API Gateway | 8765 | Request Routing & Load Balancing | — |
| Question Service | 8081 | Question Management | `questiondb` |
| Quiz Service | 8090 | Quiz Operations | `quizdb` |

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| **Language** | Java 17 LTS |
| **Framework** | Spring Boot 3.5.14 |
| **Cloud** | Spring Cloud 2025.0.0 |
| **API Gateway** | Spring Cloud Gateway |
| **Service Discovery** | Netflix Eureka |
| **HTTP Client** | OpenFeign |
| **ORM** | Spring Data JPA + Hibernate |
| **Database** | PostgreSQL 12+ |
| **Build Tool** | Maven 3.8+ |
| **Build** | Maven |
| **Utilities** | Project Lombok |

---

## 📋 Prerequisites

Verify you have the required software installed:

```bash
# Java 17+
java -version

# Maven 3.8+
mvn -version

# PostgreSQL 12+
psql --version

# Git
git --version
```

**Optional but recommended:**
- IntelliJ IDEA / VS Code
- Postman / Insomnia (API testing)
- DBeaver / pgAdmin (Database UI)

---

## 🚀 Installation

### Step 1: Clone Repository

```bash
git clone https://github.com/yourusername/quizCloud.git
cd quizCloud
```

### Step 2: Install Dependencies

```bash
mvn clean install
```

### Step 3: Database Setup

**Start PostgreSQL service:**

```bash
# Windows
net start PostgreSQL

# macOS
brew services start postgresql

# Linux
sudo systemctl start postgresql
```

**Create databases:**

```bash
psql -U postgres -c "CREATE DATABASE questiondb;"
psql -U postgres -c "CREATE DATABASE quizdb;"
```

**Load schema:**

```bash
psql -U postgres -d questiondb -f question-table-data.sql
```

**Verify installation:**

```bash
psql -U postgres -d questiondb -c "SELECT COUNT(*) FROM question;"
```

---

## ⚙️ Configuration

### Environment Setup

Update credentials in each service's `application.properties` file:

#### Question Service
**📁 `question-service/src/main/resources/application.properties`**

```properties
spring.application.name=question-service
server.port=8081
spring.datasource.url=jdbc:postgresql://localhost:5432/questiondb
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
eureka.client.service-url.defaultZone=http://localhost:8761/eureka
```

#### Quiz Service
**📁 `quiz-service/src/main/resources/application.properties`**

```properties
spring.application.name=quiz-service
server.port=8090
spring.datasource.url=jdbc:postgresql://localhost:5432/quizdb
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
eureka.client.service-url.defaultZone=http://localhost:8761/eureka
```

#### API Gateway
**📁 `api-gateway/src/main/resources/application.properties`**

```properties
spring.application.name=api-gateway
server.port=8765
spring.cloud.gateway.discovery.locator.enabled=true
spring.cloud.gateway.discovery.locator.lower-case-service-id=true
eureka.client.service-url.defaultZone=http://localhost:8761/eureka
```

#### Service Registry
**📁 `service-registry/src/main/resources/application.properties`**

```properties
spring.application.name=service-registry
server.port=8761
eureka.instance.hostname=localhost
eureka.client.fetch-registry=false
eureka.client.register-with-eureka=false
```

> ⚠️ **SECURITY WARNING**: Store sensitive credentials in environment variables, not in git.

---

## ▶️ Running Services

### Startup Order

Services must start in this sequence for proper service discovery:

#### 1️⃣ Service Registry (Eureka)

```bash
cd service-registry
mvn spring-boot:run
```

✅ **Expected:** Service available at `http://localhost:8761`

#### 2️⃣ Question Service

```bash
cd question-service
mvn spring-boot:run
```

✅ **Expected:** Service available at `http://localhost:8081`

#### 3️⃣ Quiz Service

```bash
cd quiz-service
mvn spring-boot:run
```

✅ **Expected:** Service available at `http://localhost:8090`

#### 4️⃣ API Gateway

```bash
cd api-gateway
mvn spring-boot:run
```

✅ **Expected:** Service available at `http://localhost:8765`

### Parallel Startup (4 Terminals)

Open 4 separate terminal windows:

```bash
# Terminal 1
cd service-registry && mvn spring-boot:run
```

```bash
# Terminal 2
cd question-service && mvn spring-boot:run
```

```bash
# Terminal 3
cd quiz-service && mvn spring-boot:run
```

```bash
# Terminal 4
cd api-gateway && mvn spring-boot:run
```

### Verification

**Check Eureka Dashboard:**

```bash
curl http://localhost:8761/eureka/apps
```

**Health Checks:**

```bash
curl http://localhost:8761/actuator/health
curl http://localhost:8081/actuator/health
curl http://localhost:8090/actuator/health
curl http://localhost:8765/actuator/health
```

**Access Eureka UI:**
Open browser → `http://localhost:8761`

---

## 📡 API Reference

### Base URL

```
http://localhost:8765
```

### Question Service APIs

#### Retrieve All Questions

```bash
GET /question-service/questions
```

**Response:**
```json
[
  {
    "id": 1,
    "questionTitle": "Which Java keyword is used to create a subclass?",
    "option1": "class",
    "option2": "interface",
    "option3": "extends",
    "option4": "implements",
    "rightAnswer": "extends",
    "category": "JAVA",
    "difficultylevel": "Easy"
  }
]
```

#### Get Question by ID

```bash
GET /question-service/questions/{id}
```

#### Filter by Category

```bash
GET /question-service/questions/category/{category}
```

#### Create Question

```bash
POST /question-service/questions
Content-Type: application/json

{
  "questionTitle": "Your question?",
  "option1": "Option 1",
  "option2": "Option 2",
  "option3": "Option 3",
  "option4": "Option 4",
  "rightAnswer": "Option 1",
  "category": "JAVA",
  "difficultylevel": "Easy"
}
```

#### Update Question

```bash
PUT /question-service/questions/{id}
Content-Type: application/json

{
  "questionTitle": "Updated question?",
  "option1": "Option 1",
  ...
}
```

#### Delete Question

```bash
DELETE /question-service/questions/{id}
```

### Quiz Service APIs

#### Retrieve All Quizzes

```bash
GET /quiz-service/quizzes
```

#### Get Quiz by ID

```bash
GET /quiz-service/quizzes/{id}
```

#### Create Quiz

```bash
POST /quiz-service/quizzes
Content-Type: application/json

{
  "title": "Java Basics",
  "description": "Test your Java knowledge",
  "category": "JAVA",
  "numOfQuestions": 10
}
```

#### Update Quiz

```bash
PUT /quiz-service/quizzes/{id}
Content-Type: application/json

{
  "title": "Updated Title",
  ...
}
```

#### Delete Quiz

```bash
DELETE /quiz-service/quizzes/{id}
```

#### Get Quiz Questions

```bash
GET /quiz-service/quizzes/{id}/questions
```

---

## 💻 Development

### Build Commands

```bash
# Clean and build all services
mvn clean install

# Build specific service
mvn clean install -f question-service/pom.xml

# Skip tests
mvn clean install -DskipTests

# Compile only
mvn compile
```

### Testing

```bash
# Run all tests
mvn test

# Test specific service
mvn test -f question-service/pom.xml

# Test with coverage
mvn test jacoco:report
```

### IDE Setup

**IntelliJ IDEA:**
1. Open project root directory
2. Right-click `pom.xml` → Add as Maven Project
3. Wait for indexing to complete

**VS Code:**
1. Install "Extension Pack for Java"
2. Install "Spring Boot Extension Pack"
3. Open the workspace folder

### Debug Mode

```bash
cd question-service
mvn spring-boot:run -Dspring-boot.run.arguments="--debug"
```

---

## 📋 Project Structure

```
quizCloud/
├── service-registry/              # Eureka Server (Port 8761)
│   ├── src/main/java/com/uv/serviceregistry/
│   └── src/main/resources/application.properties
│
├── api-gateway/                   # API Gateway (Port 8765)
│   ├── src/main/java/com/uv/apigateway/
│   └── src/main/resources/application.properties
│
├── question-service/              # Question APIs (Port 8081)
│   ├── src/main/java/com/uv/questionservice/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── dao/
│   │   └── model/
│   └── src/main/resources/application.properties
│
├── quiz-service/                  # Quiz APIs (Port 8090)
│   ├── src/main/java/com/uv/quizservice/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── dao/
│   │   ├── feign/
│   │   └── model/
│   └── src/main/resources/application.properties
│
├── question-table-data.sql        # Database schema & sample data
├── README.md                      # This file
└── pom.xml                        # Parent POM
```

---

## 🔗 Service Communication

### OpenFeign Client Pattern

Quiz Service communicates with Question Service using **OpenFeign**:

```java
@FeignClient(name = "question-service")
public interface QuestionClient {
    @GetMapping("/questions")
    List<Question> getAllQuestions();
    
    @GetMapping("/questions/{id}")
    Question getQuestionById(@PathVariable Long id);
    
    @GetMapping("/questions/category/{category}")
    List<Question> getQuestionsByCategory(@PathVariable String category);
}
```

**Advantages:**
- ✅ Service discovery via Eureka
- ✅ Client-side load balancing
- ✅ Built-in resilience
- ✅ Declarative API

---

## 🐛 Troubleshooting

### Port Already in Use

```bash
# Windows - Find process on port 8761
netstat -ano | findstr :8761

# Kill process
taskkill /PID <PID> /F

# macOS/Linux - Find process
lsof -i :8761

# Kill process
kill -9 <PID>
```

### PostgreSQL Connection Failed

```bash
# Check PostgreSQL status
# Windows
Get-Service postgresql*

# Verify database exists
psql -U postgres -c "\l"

# Test connection
psql -U postgres -d questiondb -c "SELECT 1;"
```

### Services Not Registering with Eureka

```bash
# Check Eureka Dashboard
curl http://localhost:8761/eureka/apps

# Verify application.properties has eureka endpoint
# Restart the service
```

### Maven Build Errors

```bash
# Update Maven dependencies
mvn clean install -U

# Clear local cache
rm -rf ~/.m2/repository

# Rebuild
mvn clean install
```

### Service Connection Issues

```bash
# Verify all services are running
curl http://localhost:8761/actuator/health
curl http://localhost:8081/actuator/health
curl http://localhost:8090/actuator/health
curl http://localhost:8765/actuator/health

# Check Eureka registration
curl http://localhost:8761/eureka/apps/QUESTION-SERVICE
```

---

## 📊 Project Statistics

- **Microservices**: 4 independent services
- **Databases**: 2 PostgreSQL instances
- **REST Endpoints**: 10+ endpoints
- **Java Version**: 17 LTS
- **Spring Boot**: 3.5.14
- **Spring Cloud**: 2025.0.0

---

## 🤝 Contributing

1. Fork the repository
2. Create feature branch: `git checkout -b feature/your-feature`
3. Commit changes: `git commit -m 'Add feature'`
4. Push branch: `git push origin feature/your-feature`
5. Open Pull Request

### Code Standards
- Follow Google Java Style Guide
- Add unit tests for new features
- Include JavaDoc for public APIs
- Keep methods single-responsibility

---

## 📄 License

This project is licensed under the **MIT License** - see LICENSE file for details.

---

## 👨‍💼 Author

**Yuvraj** - Project Development & Architecture

---

<div align="center">

Made with 💡 for modern cloud-native development

⭐ **Like this project? Give it a star!**

</div>
