# QuizCloud Microservices - Infrastructure Setup

## Overview
This document describes the complete infrastructure setup for the QuizCloud microservices ecosystem with centralized configuration management, distributed tracing, and health monitoring.

## Architecture Components

### 1. **Config Server** (Port: 8888)
Located in: `config-server/`

**Purpose**: Centralized configuration management for all microservices

**Configuration Files** (in `config-server/configs/`):
- `api-gateway.yml` - API Gateway configuration
- `question-service.yml` - Question Service configuration
- `quiz-service.yml` - Quiz Service configuration
- `service-registry.yml` - Service Registry configuration

**Key Features**:
- Uses local file system for configuration (native profile)
- Manages database connections, Eureka settings, and actuator endpoints
- All microservices fetch their configuration from this server at startup

**Startup**: 
```bash
cd config-server
./mvnw spring-boot:run
```

### 2. **Service Registry (Eureka)** (Port: 8761)
Located in: `service-registry/`

**Purpose**: Service discovery and registration for all microservices

**Configuration Sources**:
- Properties from Config Server (`service-registry.yml`)
- Local application.properties (bootstrap settings)

**Startup**:
```bash
cd service-registry
./mvnw spring-boot:run
```

### 3. **API Gateway** (Port: 8080, originally 8765)
Located in: `api-gateway/`

**Purpose**: Single entry point for all client requests with routing and tracing

**Key Features**:
- Routes requests to appropriate microservices
- **Distributed Tracing**: Enabled with Micrometer + Brave
- **RabbitMQ Integration**: Sends traces to Zipkin via RabbitMQ
- **Health Monitoring**: Actuator endpoints exposed
- **Service Discovery**: Auto-discovery of services via Eureka

**Dependencies Added**:
- `spring-boot-starter-actuator` - Health checks and metrics
- `spring-cloud-starter-config` - Config Server client
- `micrometer-tracing-bridge-brave` - Brave tracing integration
- `zipkin-sender-amqp` - Send traces to Zipkin via AMQP/RabbitMQ
- `spring-rabbit` - RabbitMQ support

**RabbitMQ Configuration**:
```properties
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```

**Tracing Configuration**:
```properties
management.tracing.sampling.probability=1.0  # Collect 100% of traces
```

**Startup**:
```bash
cd api-gateway
./mvnw spring-boot:run
```

### 4. **Question Service** (Port: 8081)
Located in: `question-service/`

**Purpose**: Manage question data and operations

**Dependencies Added**:
- `spring-boot-starter-actuator` - Health checks
- `spring-cloud-starter-config` - Config Server client

**Configuration Sources**:
- Config Server: `question-service.yml`
- Database: PostgreSQL (questiondb)

**Startup**:
```bash
cd question-service
./mvnw spring-boot:run
```

### 5. **Quiz Service** (Port: 8082)
Located in: `quiz-service/`

**Purpose**: Manage quiz operations and data

**Dependencies Added**:
- `spring-boot-starter-actuator` - Health checks
- `spring-cloud-starter-config` - Config Server client

**Configuration Sources**:
- Config Server: `quiz-service.yml`
- Database: PostgreSQL (quizdb)

**Startup**:
```bash
cd quiz-service
./mvnw spring-boot:run
```

### 6. **Zipkin Server** (Port: 9411)
Located in: `zipkin-server/`

**Purpose**: Centralized distributed tracing visualization

**Key Features**:
- **RabbitMQ Integration**: Listens for traces sent via RabbitMQ by microservices
- **Web UI**: Interactive UI to view and analyze traces at http://localhost:9411/zipkin/
- **In-Memory Storage**: Uses in-memory storage for development (configurable to MySQL/Elasticsearch for production)

**Dependencies**:
- `zipkin-server` - Zipkin server
- `zipkin-autoconfigure-ui` - Web UI
- `spring-rabbit` - RabbitMQ consumer
- `zipkin-autoconfigure-storage-rabbitmq` - RabbitMQ message ingestion

**RabbitMQ Configuration**:
```properties
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```

**Startup**:
```bash
cd zipkin-server
./mvnw spring-boot:run
```

## Configuration Flow

### 1. Bootstrap Process
```
Microservice Start
        ↓
Read bootstrap.properties
        ↓
Connect to Config Server (http://localhost:8888)
        ↓
Fetch service-specific configuration file (e.g., api-gateway.yml)
        ↓
Override local application.properties with remote config
        ↓
Register with Eureka Service Registry
```

### 2. Request Tracing Flow
```
API Gateway receives request
        ↓
Brave creates trace context
        ↓
Request processed
        ↓
Trace sent to RabbitMQ (zipkin-sender-amqp)
        ↓
Zipkin Server consumes from RabbitMQ
        ↓
Traces stored and displayed in Web UI
```

## Prerequisites

- **Java 17** or higher
- **Spring Boot 3.5.14**
- **Spring Cloud 2025.0.0**
- **RabbitMQ** running on localhost:5672 (for trace messaging)
- **PostgreSQL** running on localhost:5432 (for microservices data)

## Startup Sequence

1. **Start RabbitMQ** (ensure it's running)
```bash
rabbitmq-server
# or using Docker
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:latest
```

2. **Start PostgreSQL** (ensure it's running)
```bash
# PostgreSQL should be accessible at localhost:5432
# Create databases: questiondb, quizdb
```

3. **Start Config Server**
```bash
cd config-server
./mvnw spring-boot:run
```

4. **Start Service Registry**
```bash
cd service-registry
./mvnw spring-boot:run
# Access Eureka Dashboard: http://localhost:8761
```

5. **Start Zipkin Server**
```bash
cd zipkin-server
./mvnw spring-boot:run
# Access Zipkin UI: http://localhost:9411/zipkin/
```

6. **Start Microservices** (in any order, they'll register with Eureka)
```bash
# Terminal 1 - API Gateway
cd api-gateway
./mvnw spring-boot:run

# Terminal 2 - Question Service
cd question-service
./mvnw spring-boot:run

# Terminal 3 - Quiz Service
cd quiz-service
./mvnw spring-boot:run
```

## Accessing Services

### Configuration Management
- **Config Server**: http://localhost:8888/configserver/
- **Get API Gateway config**: http://localhost:8888/api-gateway/default
- **Get Question Service config**: http://localhost:8888/question-service/default
- **Get Quiz Service config**: http://localhost:8888/quiz-service/default

### Service Registry
- **Eureka Dashboard**: http://localhost:8761

### Monitoring and Health
- **Config Server Health**: http://localhost:8888/actuator/health
- **API Gateway Health**: http://localhost:8080/actuator/health
- **Question Service Health**: http://localhost:8081/actuator/health
- **Quiz Service Health**: http://localhost:8082/actuator/health
- **Zipkin Server Health**: http://localhost:9411/actuator/health

### Tracing
- **Zipkin UI**: http://localhost:9411/zipkin/
- View all traces, search by service name, and analyze request flows

### API Gateway Routes
- **Questions**: http://localhost:8080/questions/** → routed to question-service
- **Quizzes**: http://localhost:8080/quizzes/** → routed to quiz-service

## Configuration Management

### Adding New Configuration Properties

1. **Edit the configuration file** in `config-server/configs/`:
```yaml
# config-server/configs/api-gateway.yml
spring:
  new-property: value
```

2. **Microservice picks up changes** on next restart (no refresh endpoint configured yet)

### Centralized Properties

All shared properties can be added to the config files:
- Database configurations
- Eureka server URLs
- RabbitMQ credentials
- Logging levels
- Tracing sampling rates

## Distributed Tracing

### How It Works
1. API Gateway receives a request
2. Brave generates a trace ID and span IDs
3. Request is processed by the gateway
4. Span is completed
5. Span is serialized and sent to RabbitMQ exchange `zipkin`
6. Zipkin Server consumes from RabbitMQ
7. Traces are stored in memory and visible in Zipkin UI

### Viewing Traces
1. Go to http://localhost:9411/zipkin/
2. Select service name (e.g., "api-gateway")
3. Click "Find Traces"
4. Click on a trace to view detailed spans

### Sampling Configuration
Currently set to 100% (capture all traces):
```properties
management.tracing.sampling.probability=1.0
```

For production, reduce to 0.1 or 0.01:
```properties
management.tracing.sampling.probability=0.1  # 10%
```

## Spring Cloud Versions

- **Spring Boot**: 3.5.14
- **Spring Cloud**: 2025.0.0
- **Java**: 17
- **Maven**: 3.2.0 (via wrapper)

## Actuator Endpoints

All services expose actuator endpoints:

```
http://localhost:[port]/actuator
http://localhost:[port]/actuator/health
http://localhost:[port]/actuator/metrics
http://localhost:[port]/actuator/env
http://localhost:[port]/actuator/beans
http://localhost:[port]/actuator/configprops
```

## Troubleshooting

### Config Server not finding configuration files
- Ensure `config-server/configs/` directory contains `.yml` files
- Check `spring.cloud.config.server.native.search-locations` in config-server properties
- Verify file names match service names (e.g., `api-gateway.yml` for `api-gateway` service)

### Microservices not connecting to Config Server
- Ensure `bootstrap.properties` contains correct config server URI
- Check if config-server is running on port 8888
- Verify `spring.config.import=configserver:http://localhost:8888`

### Traces not appearing in Zipkin
- Verify RabbitMQ is running (port 5672)
- Check API Gateway logs for trace sending errors
- Ensure Zipkin Server is running on port 9411
- Verify Brave dependencies are correctly added to api-gateway pom.xml

### Services not registering with Eureka
- Verify Service Registry is running on port 8761
- Check that services have correct Eureka configuration from config server
- Check firewall/network connectivity between services

## Next Steps

1. **Enable OAuth2/Security**: Add Spring Security for API authentication
2. **Implement Config Refresh**: Add config refresh endpoints for dynamic property updates
3. **Add Metrics Export**: Configure Prometheus/Grafana for metrics visualization
4. **Database Migration**: Use Flyway/Liquibase for database versioning
5. **API Documentation**: Add Springdoc OpenAPI for interactive API docs
6. **Circuit Breaking**: Add Resilience4j for fault tolerance
7. **Rate Limiting**: Implement API rate limiting in API Gateway

## Files Created/Modified

### Created Files
- `config-server/` - New Config Server application
- `zipkin-server/` - New Zipkin Server application
- `config-server/configs/*.yml` - Configuration files for all services
- All `bootstrap.properties` files in microservices

### Modified Files
- All `pom.xml` files - Added actuator and config dependencies
- All `application.properties` files - Added actuator and management endpoints
- `api-gateway/application.properties` - Added RabbitMQ and tracing config
- `zipkin-server/application.properties` - Added RabbitMQ configuration

---

**Last Updated**: May 22, 2026
**Spring Cloud Version**: 2025.0.0
**Spring Boot Version**: 3.5.14
**Java Version**: 17
