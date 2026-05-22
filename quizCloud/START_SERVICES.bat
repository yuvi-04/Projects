@echo off
REM QuizCloud Microservices Startup Guide - Windows
REM This script demonstrates the order to start all services

cls
echo.
echo ==========================================
echo QuizCloud Microservices Startup Guide
echo ==========================================
echo.

echo PREREQUISITES:
echo 1. RabbitMQ must be running on localhost:5672
echo 2. PostgreSQL must be running on localhost:5432
echo 3. Java 17 must be installed
echo 4. Each service needs a separate terminal window
echo.

echo STARTUP SEQUENCE:
echo.

echo Step 1: Start Config Server (Port: 8888)
echo Command: cd config-server ^&^& mvnw.cmd spring-boot:run
echo Wait for: 'Started ConfigServerApplication'
echo.

echo Step 2: Start Service Registry/Eureka (Port: 8761)
echo Command: cd service-registry ^&^& mvnw.cmd spring-boot:run
echo Wait for: 'Started ServiceRegistryApplication'
echo Dashboard: http://localhost:8761
echo.

echo Step 3: Start Zipkin Server (Port: 9411)
echo Command: cd zipkin-server ^&^& mvnw.cmd spring-boot:run
echo Wait for: 'Started ZipkinServerApplication'
echo Dashboard: http://localhost:9411/zipkin/
echo.

echo Step 4: Start API Gateway (Port: 8080)
echo Command: cd api-gateway ^&^& mvnw.cmd spring-boot:run
echo Wait for: 'Netty started with reactor.netty.http.server.HttpServer'
echo.

echo Step 5: Start Question Service (Port: 8081)
echo Command: cd question-service ^&^& mvnw.cmd spring-boot:run
echo Wait for: 'Started QuestionServiceApplication'
echo.

echo Step 6: Start Quiz Service (Port: 8082)
echo Command: cd quiz-service ^&^& mvnw.cmd spring-boot:run
echo Wait for: 'Started QuizServiceApplication'
echo.

echo ==========================================
echo VERIFICATION
echo ==========================================
echo.
echo After all services are running, verify in your browser:
echo.
echo 1. Config Server Health:
echo    http://localhost:8888/actuator/health
echo.
echo 2. Eureka Dashboard:
echo    http://localhost:8761/
echo.
echo 3. Service Endpoints:
echo    - API Gateway: http://localhost:8080/actuator/health
echo    - Question Service: http://localhost:8081/actuator/health
echo    - Quiz Service: http://localhost:8082/actuator/health
echo.
echo 4. Zipkin Tracing:
echo    http://localhost:9411/zipkin/
echo.

echo ==========================================
echo QUICK LAUNCH COMMANDS (copy-paste ready)
echo ==========================================
echo.
echo For Config Server:
echo   START cmd /k "cd config-server && mvnw.cmd spring-boot:run"
echo.
echo For Service Registry:
echo   START cmd /k "cd service-registry && mvnw.cmd spring-boot:run"
echo.
echo For Zipkin Server:
echo   START cmd /k "cd zipkin-server && mvnw.cmd spring-boot:run"
echo.
echo For API Gateway:
echo   START cmd /k "cd api-gateway && mvnw.cmd spring-boot:run"
echo.
echo For Question Service:
echo   START cmd /k "cd question-service && mvnw.cmd spring-boot:run"
echo.
echo For Quiz Service:
echo   START cmd /k "cd quiz-service && mvnw.cmd spring-boot:run"
echo.

echo ==========================================
echo USEFUL COMMANDS (for PowerShell or CMD)
echo ==========================================
echo.
echo View all services in Eureka:
echo   curl http://localhost:8761/eureka/apps
echo.
echo Get API Gateway configuration:
echo   curl http://localhost:8888/api-gateway/default
echo.
echo API Gateway Health:
echo   curl http://localhost:8080/actuator/health
echo.

pause
