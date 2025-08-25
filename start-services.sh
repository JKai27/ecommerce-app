#!/bin/bash

# start-services.sh - Automatically start Docker services for development
# This script ensures all required services are running before starting the Spring Boot app

set -e  # Exit on any error

echo "🚀 Starting Shopeazy Development Services..."
echo "============================================"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}✓${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}⚠${NC} $1"
}

print_error() {
    echo -e "${RED}✗${NC} $1"
}

print_info() {
    echo -e "${BLUE}ℹ${NC} $1"
}

# Check if Docker is running
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        print_error "Docker is not running. Please start Docker Desktop and try again."
        exit 1
    fi
    print_status "Docker is running"
}

# Check if docker-compose.yml exists
check_compose_file() {
    if [ ! -f "docker-compose.yml" ]; then
        print_error "docker-compose.yml not found in current directory"
        exit 1
    fi
    print_status "Found docker-compose.yml"
}

# Start services with health checks
start_services() {
    print_info "Starting Docker services..."
    
    # Start all services in detached mode
    docker-compose up -d
    
    print_status "Services started successfully"
}

# Wait for service to be ready
wait_for_service() {
    local service=$1
    local port=$2
    local max_attempts=30
    local attempt=1
    
    print_info "Waiting for $service to be ready on port $port..."
    
    while [ $attempt -le $max_attempts ]; do
        if nc -z localhost $port 2>/dev/null; then
            print_status "$service is ready!"
            return 0
        fi
        
        echo -n "."
        sleep 2
        ((attempt++))
    done
    
    print_warning "$service is not responding on port $port (timeout after ${max_attempts} attempts)"
    return 1
}

# Health check for all services
health_check_services() {
    echo ""
    print_info "Performing health checks..."
    
    # MongoDB
    wait_for_service "MongoDB" 27017
    
    # Redis  
    wait_for_service "Redis" 6379
    
    # Kafka (Zookeeper first)
    wait_for_service "Zookeeper" 2181
    wait_for_service "Kafka" 9092
    
    # MailHog
    wait_for_service "MailHog SMTP" 1025
    wait_for_service "MailHog Web UI" 8025
    
    echo ""
    print_status "All services are ready!"
}

# Display service information
show_service_info() {
    echo ""
    echo "📋 Service Information:"
    echo "======================"
    echo "MongoDB:      mongodb://localhost:27017 (admin: mongo-admin/mongo@admin)"
    echo "Redis:        redis://localhost:6379"
    echo "Kafka:        localhost:9092"
    echo "MailHog SMTP: localhost:1025"
    echo "MailHog UI:   http://localhost:8025"
    echo "Kafka UI:     http://localhost:8080"
    echo ""
}

# Show running containers
show_running_containers() {
    print_info "Running containers:"
    docker-compose ps
    echo ""
}

# Main execution
main() {
    # Change to script directory to ensure docker-compose.yml is found
    cd "$(dirname "$0")"
    
    check_docker
    check_compose_file
    start_services
    health_check_services
    show_service_info
    show_running_containers
    
    print_status "🎉 All services are ready! You can now start your Spring Boot application."
    echo ""
    print_info "To stop services later, run: docker-compose down"
    print_info "To view logs, run: docker-compose logs -f [service-name]"
}

# Handle script interruption
cleanup() {
    echo ""
    print_warning "Script interrupted. Services may still be starting..."
    print_info "Check status with: docker-compose ps"
    exit 130
}

trap cleanup SIGINT SIGTERM

# Check for netcat availability (required for port checking)
if ! command -v nc &> /dev/null; then
    print_warning "netcat (nc) not found. Health checks may not work properly."
    print_info "Install with: brew install netcat (macOS) or apt-get install netcat (Linux)"
fi

# Run main function
main