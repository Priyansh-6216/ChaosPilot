#!/bin/bash

# ChaosPilot Day 1 Verification Script
# Checks all services are running and responding

set -e

echo "🚀 ChaosPilot Day 1 Verification"
echo "=================================="
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to check endpoint
check_health() {
    local port=$1
    local service=$2
    
    if curl -s http://localhost:$port/health > /dev/null 2>&1; then
        echo -e "${GREEN}✓${NC} $service (port $port) - ${GREEN}UP${NC}"
        return 0
    else
        echo -e "${RED}✗${NC} $service (port $port) - ${RED}DOWN${NC}"
        return 1
    fi
}

# Function to check database
check_postgres() {
    if docker exec chaospilot-postgres pg_isready -U chaospilot > /dev/null 2>&1; then
        echo -e "${GREEN}✓${NC} PostgreSQL - ${GREEN}UP${NC}"
        return 0
    else
        echo -e "${RED}✗${NC} PostgreSQL - ${RED}DOWN${NC}"
        return 1
    fi
}

# Function to check Kafka
check_kafka() {
    if docker exec chaospilot-kafka kafka-broker-api-versions --bootstrap-server localhost:9092 > /dev/null 2>&1; then
        echo -e "${GREEN}✓${NC} Kafka - ${GREEN}UP${NC}"
        return 0
    else
        echo -e "${RED}✗${NC} Kafka - ${RED}DOWN${NC}"
        return 1
    fi
}

echo "🔍 Checking Core Services..."
check_health 8080 "API Gateway" || true
check_health 8081 "Experiment Service" || true
check_health 8082 "Chaos Orchestrator" || true

echo ""
echo "🔍 Checking Demo Services..."
check_health 8083 "Order Service" || true
check_health 8084 "Payment Service" || true
check_health 8085 "Inventory Service" || true
check_health 8086 "User Service" || true
check_health 8087 "Notification Service" || true

echo ""
echo "🔍 Checking Infrastructure..."
check_postgres
check_kafka
check_health 9090 "Prometheus" || true
check_health 3000 "Grafana" || true

echo ""
echo "📊 Checking Database Tables..."
TABLES=$(docker exec chaospilot-postgres psql -U chaospilot -d chaospilot -t -c "\dt" 2>/dev/null | wc -l)
if [ $TABLES -gt 0 ]; then
    echo -e "${GREEN}✓${NC} PostgreSQL tables created"
else
    echo -e "${RED}✗${NC} PostgreSQL tables not found"
fi

echo ""
echo "📨 Checking Kafka Topics..."
TOPICS=$(docker exec chaospilot-kafka kafka-topics --bootstrap-server localhost:9092 --list 2>/dev/null | wc -l)
if [ $TOPICS -gt 0 ]; then
    echo -e "${GREEN}✓${NC} Kafka topics ($TOPICS found)"
    docker exec chaospilot-kafka kafka-topics --bootstrap-server localhost:9092 --list 2>/dev/null | head -5
else
    echo -e "${RED}✗${NC} Kafka topics not found"
fi

echo ""
echo "=================================="
echo "✅ Day 1 Verification Complete!"
echo ""
echo "📚 Next Steps:"
echo "1. Day 2: Implement Experiment CRUD APIs"
echo "2. Day 3: Implement failure injection engine"
echo "3. Day 4: Add observability dashboards"
echo "4. Day 5: Build AI RCA worker"
echo "5. Day 6: Generate reports"
echo "6. Day 7: Build React UI"
echo ""
echo "🌐 Web UIs:"
echo "   Grafana:    http://localhost:3000 (admin/admin)"
echo "   Prometheus: http://localhost:9090"
echo "   Gateway:    http://localhost:8080"
