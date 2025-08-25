#!/bin/bash

# run-with-docker.sh - Start application with all Docker services
# This is the recommended way to run in development

echo "🚀 Starting Shopeazy E-Commerce Backend (Full Development Mode)"
echo "==============================================================="

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

print_info() {
    echo -e "${BLUE}ℹ${NC} $1"
}

print_success() {
    echo -e "${GREEN}✓${NC} $1"
}

# Change to script directory
cd "$(dirname "$0")"

# Step 1: Start Docker services
print_info "Starting Docker services..."
./start-services.sh

if [ $? -ne 0 ]; then
    echo "Failed to start Docker services. Exiting."
    exit 1
fi

print_success "Docker services are ready"

# Step 2: Start Spring Boot application with dev profile
print_info "Starting Spring Boot application..."
export SPRING_PROFILES_ACTIVE=dev

# Use gradlew bootRun or java -jar depending on preference
if [ -f "./gradlew" ]; then
    print_info "Using Gradle wrapper..."
    ./gradlew bootRun --args='--spring.profiles.active=dev'
else
    print_info "Please start your application manually with profile: dev"
    print_info "Use: ./gradlew bootRun --args='--spring.profiles.active=dev'"
fi