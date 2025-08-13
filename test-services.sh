#!/bin/bash

# test-services.sh - Test which services are available

echo "🔍 Testing Service Availability"
echo "==============================="

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

test_service() {
    local service=$1
    local host=$2
    local port=$3
    
    if nc -z $host $port 2>/dev/null; then
        echo -e "  ${GREEN}✅${NC} $service ($host:$port) - Available"
        return 0
    else
        echo -e "  ${RED}❌${NC} $service ($host:$port) - Unavailable"
        return 1
    fi
}

echo "Testing Docker services:"

# Test all services
mongodb_ok=$(test_service "MongoDB" "localhost" "27017"; echo $?)
redis_ok=$(test_service "Redis" "localhost" "6379"; echo $?)
zookeeper_ok=$(test_service "Zookeeper" "localhost" "2181"; echo $?)
kafka_ok=$(test_service "Kafka" "localhost" "9092"; echo $?)
mailhog_ok=$(test_service "MailHog SMTP" "localhost" "1025"; echo $?)
mailhog_ui_ok=$(test_service "MailHog UI" "localhost" "8025"; echo $?)

echo ""
echo "📊 Service Summary:"

# Calculate totals
available=0
total=6

[ "$mongodb_ok" = "0" ] && ((available++))
[ "$redis_ok" = "0" ] && ((available++))  
[ "$zookeeper_ok" = "0" ] && ((available++))
[ "$kafka_ok" = "0" ] && ((available++))
[ "$mailhog_ok" = "0" ] && ((available++))
[ "$mailhog_ui_ok" = "0" ] && ((available++))

echo "  Available: $available/$total services"

if [ $available -eq $total ]; then
    echo -e "  ${GREEN}🎉 All services ready! Use: ./run-with-docker.sh${NC}"
elif [ "$mongodb_ok" = "0" ]; then
    echo -e "  ${YELLOW}⚠️ MongoDB available, others missing. Use: ./run-local.sh${NC}"
    echo -e "  ${YELLOW}💡 Or start all services: ./start-services.sh${NC}"
else
    echo -e "  ${RED}❌ MongoDB required. Start with: docker-compose up -d mongodb${NC}"
    echo -e "  ${YELLOW}💡 Or start all services: ./start-services.sh${NC}"
fi

echo ""
echo "🚀 Run options:"
echo "  Full development:  ./run-with-docker.sh"
echo "  Local mode:        ./run-local.sh" 
echo "  Start services:    ./start-services.sh"