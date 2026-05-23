#!/usr/bin/env bash
################################################################################
# QuizCloud Microservices Launcher - Linux/macOS
################################################################################
# This script starts all QuizCloud services in the correct startup order.
# Each service runs in its own terminal window for easy monitoring.
################################################################################

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# ============================================================================
# Color codes for output
# ============================================================================
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# ============================================================================
# Service Configuration
# ============================================================================
commands=(
  "cd \"$ROOT_DIR/config-server\" && mvn spring-boot:run"
  "cd \"$ROOT_DIR/service-registry\" && mvn spring-boot:run"
  "cd \"$ROOT_DIR/zipkin-server\" && mvn spring-boot:run"
  "cd \"$ROOT_DIR/question-service\" && mvn spring-boot:run"
  "cd \"$ROOT_DIR/quiz-service\" && mvn spring-boot:run"
  "cd \"$ROOT_DIR/api-gateway\" && mvn spring-boot:run"
)

titles=(
  "🟢 Config Server :8888"
  "🟡 Service Registry :8761"
  "🟣 Zipkin Server :9411"
  "🔵 Question Service :8081"
  "🟠 Quiz Service :8082"
  "🔴 API Gateway :8080"
)

delays=(12 10 8 5 5 0)

# ============================================================================
# Print banner
# ============================================================================
clear
echo ""
echo "╔════════════════════════════════════════════════════════════════════════╗"
echo "║                                                                        ║"
echo "║        ☁️  QuizCloud Microservices Platform - Linux/macOS Launcher    ║"
echo "║                                                                        ║"
echo "╚════════════════════════════════════════════════════════════════════════╝"
echo ""

# ============================================================================
# Prerequisites display
# ============================================================================
echo -e "${CYAN}✓ Prerequisites Check:${NC}"
echo ""
echo "  [✓] PostgreSQL running on localhost:5432 with:"
echo "      - Database: questiondb (with data)"
echo "      - Database: quizdb"
echo "      - Credentials: postgres/password"
echo ""
echo "  [✓] RabbitMQ running on localhost:5672"
echo "      To start: docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management"
echo ""
echo "  [✓] Java 17+ and Maven available"
echo "      To verify: java -version && mvn -version"
echo ""
echo "  [?] Optional - Prometheus/Grafana monitoring stack"
echo "      To start: cd monitoring && docker compose up -d"
echo ""

# ============================================================================
# Function to open terminal
# ============================================================================
open_terminal() {
  local title="$1"
  local command="$2"

  if command -v gnome-terminal >/dev/null 2>&1; then
    gnome-terminal --title="$title" -- bash -lc "$command; exec bash" &
    return 0
  elif command -v konsole >/dev/null 2>&1; then
    konsole --new-tab --title "$title" -e bash -lc "$command; exec bash" &
    return 0
  elif command -v xterm >/dev/null 2>&1; then
    xterm -T "$title" -e bash -lc "$command; exec bash" &
    return 0
  elif command -v osascript >/dev/null 2>&1; then
    osascript -e "tell application \"Terminal\" to do script \"$command\"" &
    return 0
  else
    return 1
  fi
}

# ============================================================================
# Start services
# ============================================================================
echo -e "${GREEN}🚀 Starting QuizCloud Services...${NC}"
echo ""

terminal_found=true

for i in "${!commands[@]}"; do
  service_num=$((i + 1))
  echo -e "${YELLOW}Phase $service_num: ${titles[$i]}${NC}"
  
  if ! open_terminal "${titles[$i]}" "${commands[$i]}"; then
    terminal_found=false
    break
  fi
  
  if [[ "${delays[$i]}" != "0" ]]; then
    echo "⏳ Waiting ${delays[$i]} seconds for service startup..."
    sleep "${delays[$i]}"
  fi
  echo ""
done

# ============================================================================
# Display results
# ============================================================================
echo ""
echo "╔════════════════════════════════════════════════════════════════════════╗"
echo "║                                                                        ║"
echo "║                    ✨ Service Launch Complete! ✨                      ║"
echo "║                                                                        ║"
echo "╚════════════════════════════════════════════════════════════════════════╝"
echo ""

if [[ "$terminal_found" == false ]]; then
  echo -e "${RED}⚠️  No supported terminal emulator found!${NC}"
  echo ""
  echo "Please start these services manually in separate terminals:"
  echo ""
  for i in "${!commands[@]}"; do
    echo -e "${CYAN}${titles[$i]}:${NC}"
    echo "  ${commands[$i]}"
    echo ""
  done
else
  echo -e "${CYAN}📊 Monitoring & Dashboards:${NC}"
  echo ""
  echo "  📋 Eureka Service Registry   http://localhost:8761"
  echo "  🔍 Zipkin Distributed Tracing http://localhost:9411"
  echo "  📊 Prometheus Metrics        http://localhost:9090"
  echo "  📈 Grafana Dashboards        http://localhost:3000 (admin/admin)"
  echo "  🐰 RabbitMQ Management       http://localhost:15672 (guest/guest)"
  echo ""
  echo -e "${CYAN}🏥 Health Checks:${NC}"
  echo ""
  echo "  Config Server:   curl http://localhost:8888/actuator/health"
  echo "  Service Registry: curl http://localhost:8761/eureka/apps"
  echo "  API Gateway:     curl http://localhost:8080/actuator/health"
  echo "  Question Service: curl http://localhost:8081/actuator/health"
  echo "  Quiz Service:    curl http://localhost:8082/actuator/health"
  echo "  Zipkin Server:   curl http://localhost:9411/health"
  echo ""
  echo -e "${GREEN}⏳ Please wait 30-60 seconds for all services to fully boot.${NC}"
  echo -e "${YELLOW}📝 Each service is running in its own terminal window above.${NC}"
  echo ""
fi

echo -e "${GREEN}🚀 Happy quizzing! 🎯✨${NC}"
echo ""
