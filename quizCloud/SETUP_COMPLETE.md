# QuizCloud Microservices - Complete Setup Summary

## ✅ What Was Implemented

This document summarizes all the infrastructure setup and configurations that have been completed for the QuizCloud microservices ecosystem.

### 1. **Spring Cloud Config Server** ✓
- **New Service**: `config-server/` (Port: 8888)
- **Purpose**: Centralized configuration management for all microservices
- **Configuration Files**: Located in `config-server/configs/`
  - `api-gateway.yml` - API Gateway configuration with gateway routes
  - `question-service.yml` - Question Service configuration
  - `quiz-service.yml` - Quiz Service configuration
  - `service-registry.yml` - Service Registry configuration
- **Profile**: Uses `native` profile with local file system for easy development
- **Access**: http://localhost:8888

### 2. **Spring Boot Actuator** ✓
Added to all services with full endpoint exposure:
- `api-gateway/pom.xml` ✓
- `question-service/pom.xml` ✓
- `quiz-service/pom.xml` ✓
- `service-registry/pom.xml` ✓
- `config-server/pom.xml` ✓

Exposed endpoints:
- Health checks: `/actuator/health`
- Metrics: `/actuator/metrics`
- Environment: `/actuator/env`
- Beans: `/actuator/beans`
- And more (all endpoints exposed)

### 3. **Spring Cloud Config Client** ✓
Added to all microservices:
- `api-gateway/` - Dependency added, bootstrap.properties created
- `question-service/` - Dependency added, bootstrap.properties created
- `quiz-service/` - Dependency added, bootstrap.properties created
- `service-registry/` - Dependency added, bootstrap.properties created

Bootstrap Configuration:
```properties
spring.cloud.config.uri=http://localhost:8888
spring.config.import=configserver:http://localhost:8888
spring.cloud.config.fail-fast=true
```

### 4. **Zipkin Server for Distributed Tracing** ✓
- **New Service**: `zipkin-server/` (Port: 9411)
- **Purpose**: Centralized trace visualization and analysis
- **Key Features**:
  - Web UI for viewing traces
  - RabbitMQ message consumption (receives traces from api-gateway)
  - In-memory storage (configurable for production)
- **Access**: http://localhost:9411/zipkin/

### 5. **Distributed Tracing in API Gateway** ✓
Added dependencies:
- `io.micrometer:micrometer-tracing-bridge-brave` - Brave tracing integration
- `io.zipkin.reporter2:zipkin-sender-amqp` - Send traces via RabbitMQ
- `org.springframework.amqp:spring-rabbit` - RabbitMQ client

Configuration:
```properties
management.tracing.sampling.probability=1.0  # Capture 100% of traces
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```

### 6. **RabbitMQ Integration** ✓
- **API Gateway** → Sends traces to RabbitMQ exchange (via zipkin-sender-amqp)
- **Zipkin Server** → Consumes traces from RabbitMQ queue
- Both configured for `localhost:5672` with `guest/guest` credentials

## 📋 Project Structure

```
quizCloud/
├── config-server/                          [NEW] Config Server
│   ├── src/main/java/.../ConfigServerApplication.java
│   ├── src/main/resources/application.properties
│   ├── configs/                            [NEW] Configuration Files
│   │   ├── api-gateway.yml
│   │   ├── question-service.yml
│   │   ├── quiz-service.yml
│   │   └── service-registry.yml
│   └── pom.xml                             [MODIFIED] Added dependencies
│
├── zipkin-server/                          [NEW] Zipkin Server
│   ├── src/main/java/.../ZipkinServerApplication.java
│   ├── src/main/resources/application.properties
│   └── pom.xml
│
├── api-gateway/
│   ├── src/main/resources/
│   │   ├── application.properties           [MODIFIED] Added tracing config
│   │   └── bootstrap.properties             [NEW] Config Server client config
│   └── pom.xml                             [MODIFIED] Added tracing dependencies
│
├── question-service/
│   ├── src/main/resources/
│   │   ├── application.properties           [MODIFIED] Added actuator config
│   │   └── bootstrap.properties             [NEW] Config Server client config
│   └── pom.xml                             [MODIFIED] Added actuator & config
│
├── quiz-service/
│   ├── src/main/resources/
│   │   ├── application.properties           [MODIFIED] Added actuator config
│   │   └── bootstrap.properties             [NEW] Config Server client config
│   └── pom.xml                             [MODIFIED] Added actuator & config
│
├── service-registry/
│   ├── src/main/resources/
│   │   ├── application.properties           [MODIFIED] Added actuator config
│   │   └── bootstrap.properties             [NEW] Config Server client config
│   └── pom.xml                             [MODIFIED] Added actuator & config
│
├── INFRASTRUCTURE_SETUP.md                  [NEW] Detailed infrastructure docs
├── START_SERVICES.sh                        [NEW] Linux/Mac startup guide
└── START_SERVICES.bat                       [NEW] Windows startup guide
```

## 🚀 Quick Start Guide

### Prerequisites
```bash
# 1. RabbitMQ (for trace messaging)
docker run -d --name rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  rabbitmq:latest

# 2. PostgreSQL (for microservices data)
docker run -d --name postgres \
  -p 5432:5432 \
  -e POSTGRES_PASSWORD=postgres \
  postgres:latest

# 3. Create databases (from PostgreSQL)
# psql -U postgres -c "CREATE DATABASE questiondb;"
# psql -U postgres -c "CREATE DATABASE quizdb;"
```

### Startup Sequence (in order)

**Terminal 1 - Config Server**
```bash
cd config-server
./mvnw spring-boot:run
# Wait for: Started ConfigServerApplication
```

**Terminal 2 - Service Registry**
```bash
cd service-registry
./mvnw spring-boot:run
# Wait for: Started ServiceRegistryApplication
# Access: http://localhost:8761
```

**Terminal 3 - Zipkin Server**
```bash
cd zipkin-server
./mvnw spring-boot:run
# Wait for: Started ZipkinServerApplication
# Access: http://localhost:9411/zipkin/
```

**Terminal 4 - API Gateway**
```bash
cd api-gateway
./mvnw spring-boot:run
# Access: http://localhost:8080
```

**Terminal 5 - Question Service**
```bash
cd question-service
./mvnw spring-boot:run
# Access: http://localhost:8081
```

**Terminal 6 - Quiz Service**
```bash
cd quiz-service
./mvnw spring-boot:run
# Access: http://localhost:8082
```

### Windows Users
Use the provided batch files:
```bash
# In the root directory
START_SERVICES.bat
```

This opens the startup guide with copy-paste commands.

## 📊 Service Ports and URLs

| Service | Port | Health | Config | Dashboard |
|---------|------|--------|--------|-----------|
| Config Server | 8888 | `/actuator/health` | - | http://localhost:8888 |
| Service Registry | 8761 | `/actuator/health` | `/api/config` | http://localhost:8761 |
| API Gateway | 8080 | `/actuator/health` | Fetch from 8888 | N/A |
| Question Service | 8081 | `/actuator/health` | Fetch from 8888 | N/A |
| Quiz Service | 8082 | `/actuator/health` | Fetch from 8888 | N/A |
| Zipkin Server | 9411 | `/actuator/health` | - | http://localhost:9411/zipkin/ |

## 🔄 Configuration Flow

```
Microservice starts
    ↓
Reads bootstrap.properties
    ↓
Connects to Config Server (8888)
    ↓
Fetches config file (e.g., api-gateway.yml)
    ↓
Overrides local properties with remote config
    ↓
Registers with Eureka (Service Registry)
    ↓
Ready to serve requests
```

## 📡 Tracing Flow

```
Request arrives at API Gateway (8080)
    ↓
Brave creates Trace Context and Span IDs
    ↓
Request is routed to target microservice
    ↓
Response sent back to client
    ↓
Span is serialized to JSON
    ↓
Sent to RabbitMQ exchange 'zipkin'
    ↓
Zipkin Server consumes from RabbitMQ
    ↓
Traces stored in memory
    ↓
View in Zipkin UI: http://localhost:9411/zipkin/
```

## 🧪 Testing the Setup

### 1. Verify all services are registered in Eureka
```bash
curl http://localhost:8761/eureka/apps
```
You should see all 4 microservices registered.

### 2. Get configuration from Config Server
```bash
curl http://localhost:8888/api-gateway/default
```
This returns the API Gateway configuration in JSON format.

### 3. Check service health
```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8761/actuator/health
curl http://localhost:9411/actuator/health
```
All should return: `{"status":"UP"}`

### 4. Generate a trace
```bash
curl http://localhost:8080/actuator/health
```

### 5. View trace in Zipkin
1. Open http://localhost:9411/zipkin/
2. Select service "api-gateway" from dropdown
3. Click "Find Traces"
4. Click on the trace to see detailed spans

## 📚 Technology Stack

| Component | Version |
|-----------|---------|
| Java | 17 |
| Spring Boot | 3.5.14 |
| Spring Cloud | 2025.0.0 |
| Maven | 3.2.0 (wrapper) |
| RabbitMQ | (latest) |
| PostgreSQL | (latest) |

## 🔧 Key Dependencies Added

### All Services
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

### API Gateway (Additional)
```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-brave</artifactId>
</dependency>
<dependency>
    <groupId>io.zipkin.reporter2</groupId>
    <artifactId>zipkin-sender-amqp</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.amqp</groupId>
    <artifactId>spring-rabbit</artifactId>
</dependency>
```

### Zipkin Server
```xml
<dependency>
    <groupId>io.zipkin.java</groupId>
    <artifactId>zipkin-server</artifactId>
</dependency>
<dependency>
    <groupId>io.zipkin.java</groupId>
    <artifactId>zipkin-autoconfigure-ui</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.amqp</groupId>
    <artifactId>spring-rabbit</artifactId>
</dependency>
<dependency>
    <groupId>io.zipkin.java</groupId>
    <artifactId>zipkin-autoconfigure-storage-rabbitmq</artifactId>
</dependency>
```

## ❓ FAQ

**Q: Why do I need RabbitMQ?**
A: RabbitMQ acts as a message broker for distributing traces from the API Gateway to Zipkin Server without adding latency to requests.

**Q: Can I use a different message broker instead of RabbitMQ?**
A: Yes, but you'd need to change the zipkin-sender dependency and configure accordingly.

**Q: What if I don't want distributed tracing?**
A: Remove the tracing dependencies from api-gateway pom.xml and the RabbitMQ configuration.

**Q: How do I change sampling probability?**
A: Edit `api-gateway/src/main/resources/application.properties`:
```properties
management.tracing.sampling.probability=0.1  # Sample 10% of traces
```

**Q: Can I use a Git repository for configuration instead of local files?**
A: Yes, modify config-server/src/main/resources/application.properties to use git instead of native profile.

**Q: What happens if Config Server is down?**
A: With `spring.cloud.config.fail-fast=true`, microservices will fail to start if Config Server is unavailable.

**Q: How do I add new configuration properties?**
A: Edit the corresponding .yml file in `config-server/configs/` and restart the microservice (no refresh endpoint yet).

## 📖 Documentation Files

- **INFRASTRUCTURE_SETUP.md** - Detailed infrastructure documentation
- **START_SERVICES.sh** - Linux/Mac startup guide
- **START_SERVICES.bat** - Windows startup guide (use this for Windows)
- **README.md** - Original project README

## 🎯 Next Steps

1. **Test the complete setup** following the Quick Start Guide
2. **View traces in Zipkin** to understand request flow
3. **Monitor services** via actuator endpoints
4. **Add more configurations** to centralized config files as needed
5. **Implement config refresh** for dynamic property updates (future enhancement)
6. **Add OAuth2/Security** for API authentication
7. **Set up CI/CD pipeline** for automated deployment

## 📝 Notes

- All services use **Spring Boot 3.5.14** and **Java 17**
- Spring Cloud version is **2025.0.0**
- Configuration follows the latest Spring Cloud best practices
- Tracing is enabled at **100% sampling** for development (reduce for production)
- All actuator endpoints are exposed for monitoring (restrict in production)

## 🆘 Troubleshooting

### Services won't start
- Check if Config Server is running on port 8888
- Check if `config-server/configs/` contains the configuration files
- Check if `bootstrap.properties` exists in all microservices

### Traces not showing in Zipkin
- Verify RabbitMQ is running (`docker ps`)
- Check API Gateway logs for AMQP connection errors
- Ensure trace sampling probability is > 0

### Services not registering in Eureka
- Check if Service Registry is running on port 8761
- Check logs for Eureka registration errors
- Verify network connectivity between services

### Configuration not loading
- Check Config Server logs for file reading errors
- Verify YAML file names match service names exactly
- Check for YAML syntax errors

---

**Created**: May 22, 2026
**Spring Boot Version**: 3.5.14
**Spring Cloud Version**: 2025.0.0
**Java Version**: 17

For detailed information, see **INFRASTRUCTURE_SETUP.md**
