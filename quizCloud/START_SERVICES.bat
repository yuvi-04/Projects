@echo off
REM ============================================================================
REM QuizCloud Microservices Launcher - Windows
REM ============================================================================
REM This script starts all QuizCloud services in the correct order
REM Each service runs in its own terminal window for easy monitoring
REM ============================================================================

setlocal enabledelayedexpansion
cd /d "%~dp0"

cls
echo.
echo ============================================================================
echo     ^^|^|  QuizCloud Microservices Platform - Windows Launcher  ^^|^|
echo ============================================================================
echo.
echo 🔵 Starting QuizCloud services in the correct sequence...
echo.

REM ============================================================================
REM Display Prerequisites Check
REM ============================================================================
echo ✓ Prerequisites Check:
echo.
echo   [✓] PostgreSQL running on localhost:5432 with:
echo       - Database: questiondb (with data)
echo       - Database: quizdb
echo       - Credentials: postgres/password
echo.
echo   [✓] RabbitMQ running on localhost:5672
echo       To start: docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
echo.
echo   [✓] Java 17+ and Maven available
echo       To verify: java -version ^&^& mvn -version
echo.
echo   [?] Optional - Prometheus/Grafana monitoring stack
echo       To start: cd monitoring ^&^& docker compose up -d
echo.

REM ============================================================================
REM Service Startup with Delays
REM ============================================================================

echo ============================================================================
echo Phase 1: Starting Config Server (Port 8888)
echo ============================================================================
START "🟢 Config Server :8888" cmd /k "cd /d !CD!\config-server && color 0A && echo [Config Server Starting...] && mvn spring-boot:run"
timeout /t 12 /nobreak >nul

echo.
echo ============================================================================
echo Phase 2: Starting Service Registry (Port 8761)
echo ============================================================================
START "🟡 Service Registry :8761" cmd /k "cd /d !CD!\service-registry && color 0B && echo [Service Registry Starting...] && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo.
echo ============================================================================
echo Phase 3: Starting Zipkin Server (Port 9411)
echo ============================================================================
START "🟣 Zipkin Server :9411" cmd /k "cd /d !CD!\zipkin-server && color 0E && echo [Zipkin Server Starting...] && mvn spring-boot:run"
timeout /t 8 /nobreak >nul

echo.
echo ============================================================================
echo Phase 4: Starting Question Service (Port 8081)
echo ============================================================================
START "🔵 Question Service :8081" cmd /k "cd /d !CD!\question-service && color 0C && echo [Question Service Starting...] && mvn spring-boot:run"
timeout /t 5 /nobreak >nul

echo.
echo ============================================================================
echo Phase 5: Starting Quiz Service (Port 8082)
echo ============================================================================
START "🟠 Quiz Service :8082" cmd /k "cd /d !CD!\quiz-service && color 03 && echo [Quiz Service Starting...] && mvn spring-boot:run"
timeout /t 5 /nobreak >nul

echo.
echo ============================================================================
echo Phase 6: Starting API Gateway (Port 8080)
echo ============================================================================
START "🔴 API Gateway :8080" cmd /k "cd /d !CD!\api-gateway && color 0D && echo [API Gateway Starting...] && mvn spring-boot:run"

echo.
echo ============================================================================
echo ✨ QuizCloud Services Launch Complete!
echo ============================================================================
echo.
echo 📊 Monitoring & Dashboards:
echo.
echo   📋 Eureka Service Registry:   http://localhost:8761
echo   🔍 Zipkin Distributed Tracing: http://localhost:9411
echo   📊 Prometheus Metrics:         http://localhost:9090
echo   📈 Grafana Dashboards:         http://localhost:3000 (admin/admin)
echo   🐰 RabbitMQ Management:        http://localhost:15672 (guest/guest)
echo.
echo 🏥 Health Checks:
echo.
echo   curl http://localhost:8888/actuator/health      (Config Server)
echo   curl http://localhost:8761/eureka/apps          (Service Registry)
echo   curl http://localhost:8080/actuator/health      (API Gateway)
echo   curl http://localhost:8081/actuator/health      (Question Service)
echo   curl http://localhost:8082/actuator/health      (Quiz Service)
echo.
echo ⏳ Please wait 30-60 seconds for all services to fully boot.
echo 📝 Each service will open in its own window above.
echo.
echo 🚀 Happy quizzing! 🎯✨
echo.
echo ============================================================================
echo.

pause
endlocal
echo   API Gateway:   http://localhost:8080/actuator/health
echo   Question:      http://localhost:8081/actuator/health
echo   Quiz:          http://localhost:8082/actuator/health
echo   Zipkin:        http://localhost:9411/zipkin/
echo   Prometheus:    http://localhost:9090
echo   Grafana:       http://localhost:3000
echo.

pause

