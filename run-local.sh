#!/bin/bash

# run-local.sh - Start application with minimal dependencies (no Docker required)
# Use this when you want to quickly test without external services

echo "🏠 Starting Shopeazy E-Commerce Backend (Local Mode - No Docker)"
echo "================================================================"

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

print_info() {
    echo -e "${BLUE}ℹ${NC} $1"
}

print_success() {
    echo -e "${GREEN}✓${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}⚠${NC} $1"
}

# Change to script directory
cd "$(dirname "$0")"

print_info "Running in LOCAL mode - external services will be mocked"
print_warning "MongoDB is still required. Make sure it's running on localhost:27017"

# Check if MongoDB is running
if ! nc -z localhost 27017 2>/dev/null; then
    print_warning "MongoDB is not running on localhost:27017"
    print_info "Start MongoDB with: docker run -d -p 27017:27017 --name mongo -e MONGO_INITDB_ROOT_USERNAME=mongo-admin -e MONGO_INITDB_ROOT_PASSWORD=mongo@admin mongo:7.0"
fi

print_info "Starting Spring Boot application in LOCAL mode..."
print_info "Features disabled: Kafka, MailHog, Redis"
print_info "Features mocked: Email sending, Message queuing, Caching"

# Set local profile
export SPRING_PROFILES_ACTIVE=local

# Start application
if [ -f "./gradlew" ]; then
    print_info "Using Gradle wrapper..."
    ./gradlew bootRun --args='--spring.profiles.active=local'
else
    print_info "Please start your application manually with profile: local"  
    print_info "Use: ./gradlew bootRun --args='--spring.profiles.active=local'"
fi