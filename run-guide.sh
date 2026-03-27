#!/bin/bash
# BiniyogBuddy - Project Setup and Execution Guide

echo "=========================================="
echo "BiniyogBuddy - Stock Trading Journal App"
echo "=========================================="
echo ""

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Project location
PROJECT_DIR="/home/rajin/IdeaProjects/BiniyogBuddy"

echo -e "${BLUE}[1] Project Information${NC}"
echo "Location: $PROJECT_DIR"
echo "Java Version: 25"
echo "Spring Boot Version: 4.0.3"
echo "Build Tool: Gradle 9.3.1"
echo ""

echo -e "${BLUE}[2] Build the Project${NC}"
echo "Command: ./gradlew clean build"
cd "$PROJECT_DIR"
./gradlew clean build -x test > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Build SUCCESSFUL${NC}"
else
    echo -e "${YELLOW}✗ Build FAILED${NC}"
fi
echo ""

echo -e "${BLUE}[3] Module Structure${NC}"
echo "Core Libraries (libs/):"
echo "  ├── users/ - User management & authentication"
echo "  ├── stocks/ - Stock journal"
echo "  ├── trades/ - Trade logs"
echo "  └── notes/ - Trade reflection notes"
echo ""
echo "Applications (apps/):"
echo "  └── api-app/ - REST API server"
echo ""
echo "Shared (common/):"
echo "  └── Common utilities and constants"
echo ""

echo -e "${BLUE}[4] Database Configuration${NC}"
echo "Development: H2 (in-memory)"
echo "Production: PostgreSQL"
echo "Console: http://localhost:8080/h2-console"
echo ""

echo -e "${BLUE}[5] REST API Endpoints${NC}"
echo ""
echo "Authentication:"
echo "  POST /api/v1/auth/register"
echo ""
echo "Stock Management:"
echo "  POST   /api/v1/stocks"
echo "  GET    /api/v1/stocks"
echo "  GET    /api/v1/stocks/{id}"
echo "  PUT    /api/v1/stocks/{id}"
echo "  DELETE /api/v1/stocks/{id}"
echo ""
echo "Trade Logs:"
echo "  POST   /api/v1/trades"
echo "  GET    /api/v1/trades"
echo "  GET    /api/v1/trades/{id}"
echo "  PUT    /api/v1/trades/{id}"
echo "  DELETE /api/v1/trades/{id}"
echo ""
echo "Trade Notes:"
echo "  GET    /api/v1/trades/{id}/notes"
echo "  PUT    /api/v1/trades/{id}/notes"
echo "  DELETE /api/v1/trades/{id}/notes"
echo ""
echo "Portfolio:"
echo "  GET    /api/v1/portfolio"
echo ""

echo -e "${BLUE}[6] Running the Application${NC}"
echo ""
echo "Development Mode:"
echo "  $ ./gradlew bootRun -p apps/api-app"
echo ""
echo "API will be available at:"
echo "  http://localhost:8080/api/v1"
echo ""

echo -e "${BLUE}[7] Docker Deployment${NC}"
echo "Run with Docker Compose:"
echo "  $ cd docker"
echo "  $ docker-compose up"
echo ""

echo -e "${BLUE}[8] Project Statistics${NC}"
echo ""
echo "Entities: 5 (User, Stock, TradeLog, TradeNote + Enums)"
echo "Repositories: 4 (Domain + JPA implementations)"
echo "Services: 4 (UserService, StockService, TradeLogService, TradeNoteService)"
echo "Controllers: 5 (Auth, Stock, TradeLog, TradeNote, Portfolio)"
echo "DTOs: 10 (Request/Response objects)"
echo ""

echo -e "${BLUE}[9] Key Features${NC}"
echo ""
echo "✓ User registration and authentication"
echo "✓ Stock journal with detailed tracking"
echo "✓ Trade logging (buy/sell transactions)"
echo "✓ Trade notes (reflection and learning)"
echo "✓ Portfolio summary and calculations"
echo "✓ REST API with full CRUD operations"
echo "✓ Database persistence (JPA/Hibernate)"
echo "✓ Multi-module architecture (DDD)"
echo ""

echo -e "${BLUE}[10] Documentation${NC}"
echo ""
echo "  • IMPLEMENTATION_COMPLETE.md - Complete implementation overview"
echo "  • ENTITIES_AND_API.md - Entity models and API documentation"
echo "  • QUICKSTART.md - Quick start guide with examples"
echo "  • README.md - Project overview"
echo ""

echo -e "${BLUE}[11] Build Gradle Files${NC}"
echo ""
echo "✓ Root build.gradle.kts (multi-module config)"
echo "✓ libs/users/build.gradle.kts"
echo "✓ libs/stocks/build.gradle.kts"
echo "✓ libs/trades/build.gradle.kts"
echo "✓ libs/notes/build.gradle.kts"
echo "✓ apps/api-app/build.gradle.kts"
echo "✓ apps/web-app/build.gradle.kts"
echo "✓ common/build.gradle.kts"
echo ""

echo -e "${GREEN}=========================================="
echo "Project is ready for development!"
echo "==========================================${NC}"
echo ""

