# 🚀 Development Setup Guide

## Quick Start Options

### Option 1: Full Development Mode (Recommended)
**All services available with Docker:**
```bash
./run-with-docker.sh
```
This will:
- Start all Docker services (MongoDB, Redis, Kafka, MailHog)
- Wait for services to be ready
- Start Spring Boot app with `dev` profile
- All features enabled

### Option 2: Local Development Mode  
**Minimal dependencies, no Docker required:**
```bash
./run-local.sh
```
This will:
- Use mock implementations for external services
- Only requires MongoDB (can be Docker or local)
- Start Spring Boot app with `local` profile  
- Kafka, Redis, MailHog features disabled/mocked

## Manual Setup

### Start Only Docker Services
```bash
./start-services.sh
```

### Start App Manually
```bash
# With all services (dev profile)
./gradlew bootRun --args='--spring.profiles.active=dev'

# Local mode (local profile)  
./gradlew bootRun --args='--spring.profiles.active=local'

# Default (uses dev profile from application.properties)
./gradlew bootRun
```

## Service Information

### Docker Services (dev profile):
- **MongoDB**: `localhost:27017` (admin: mongo-admin/mongo@admin)
- **Redis**: `localhost:6379`
- **Kafka**: `localhost:9092`
- **Zookeeper**: `localhost:2181`
- **MailHog SMTP**: `localhost:1025`
- **MailHog Web UI**: http://localhost:8025
- **Kafka UI**: http://localhost:8080

### Application Endpoints:
- **API**: http://localhost:8080/api
- **Health Check**: http://localhost:8080/actuator/health
- **Swagger/OpenAPI**: http://localhost:8080/swagger-ui.html (if configured)

## Profile Comparison

| Feature | `dev` Profile | `local` Profile |
|---------|---------------|-----------------|
| MongoDB | ✅ Required | ✅ Required |
| Redis | ✅ Docker | ❌ Mocked (in-memory) |
| Kafka | ✅ Docker | ❌ Mocked (console logs) |
| MailHog | ✅ Docker | ❌ Mocked (console logs) |
| Full Features | ✅ Yes | ⚠️ Limited |
| Docker Required | ✅ Yes | ❌ No (except MongoDB) |
| Best For | Full development | Quick testing |

## Troubleshooting

### ❌ "Connection refused" errors
**Problem**: External services not running
**Solution**: 
```bash
# Check what's running
docker-compose ps

# Start services
./start-services.sh

# Or switch to local mode
./run-local.sh
```

### ❌ "Port already in use" errors
**Problem**: Services already running or port conflicts
**Solution**:
```bash
# Stop all services
docker-compose down

# Check what's using ports
lsof -i :8080  # Spring Boot
lsof -i :9092  # Kafka
lsof -i :27017 # MongoDB

# Kill process if needed
kill -9 <PID>
```

### ❌ MongoDB connection errors
**Problem**: MongoDB authentication or connectivity
**Solutions**:
```bash
# Reset MongoDB container
docker-compose down
docker volume rm ecommerce-backend_mongodb_data
docker-compose up -d mongodb

# Or use MongoDB without auth for testing
# (Update connection string in application.properties)
```

### ❌ Slow startup or timeouts
**Problem**: Services taking time to start
**Solutions**:
- Wait longer (first startup downloads images)
- Increase timeout values in application.properties
- Use `local` profile for faster startup
- Check Docker Desktop resources (CPU/Memory)

## Development Workflow

### Day-to-Day Development:
1. **Morning**: `./run-with-docker.sh` (starts everything)
2. **Code & Test**: Full feature development
3. **Evening**: `docker-compose down` (stops services)

### Quick Testing:
1. **Quick Start**: `./run-local.sh` (minimal dependencies)
2. **Test Feature**: Limited but fast startup
3. **Switch to Full**: `./run-with-docker.sh` when needed

### CI/CD Pipeline:
- Use `local` profile for fast builds/tests
- Use `dev` profile for integration tests
- Production uses separate `prod` profile

## Monitoring & Debugging

### View Service Logs:
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f kafka
docker-compose logs -f mailhog
```

### Check Service Health:
```bash
# Application health
curl http://localhost:8080/actuator/health

# Individual services
curl -f http://localhost:8025  # MailHog UI
curl -f http://localhost:8080  # Kafka UI (if running)
```

### Debug Application:
- Set `logging.level.shopeazy.com.ecommerce_app=DEBUG`
- Check application startup logs for service connectivity
- Use correlation IDs in error responses for tracing

## IDE Configuration

### IntelliJ IDEA:
1. **Run Configuration**: 
   - Main class: `com.shopeazy.ecommerce_app.ECommerceAppApplication`
   - VM options: `-Dspring.profiles.active=dev`
   - Environment variables: `SPRING_PROFILES_ACTIVE=dev`

2. **Database**: 
   - Add MongoDB data source: `mongodb://mongo-admin:mongo@admin@localhost:27017/ecommerce-app`

### VS Code:
1. **launch.json**:
```json
{
    "type": "java",
    "name": "ECommerceApp (dev)",
    "request": "launch",
    "mainClass": "com.shopeazy.ecommerce_app.ECommerceAppApplication",
    "env": {
        "SPRING_PROFILES_ACTIVE": "dev"
    }
}
```

## Best Practices

1. **Always check service health** on startup
2. **Use correlation IDs** for debugging (automatically added)
3. **Monitor Docker resources** (don't let them consume all memory)
4. **Clean up regularly**: `docker system prune` (removes unused images/containers)
5. **Use appropriate profile** for your task (dev vs local)

## Getting Help

### Check Application Health:
```bash
curl http://localhost:8080/actuator/health | jq
```

### View Configuration:
```bash
# See active profiles and properties
curl http://localhost:8080/actuator/info
```

### Common Commands:
```bash
# Reset everything
docker-compose down -v
docker system prune -f
./run-with-docker.sh

# Quick restart
docker-compose restart
./gradlew bootRun
```