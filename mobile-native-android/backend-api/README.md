# Edham Logistics Backend API

## 📋 Overview

Edham Logistics Backend API is a comprehensive, production-ready logistics management system built with Spring Boot, PostgreSQL, and Redis. This system provides complete functionality for managing shipments, users, tracking, invoices, and more with enterprise-grade security and performance.

## 🏗️ Architecture

### Technology Stack
- **Backend Framework**: Spring Boot 3.x
- **Database**: PostgreSQL 14+ with Redis caching
- **Security**: JWT authentication with role-based access control
- **Real-time**: WebSocket support for live tracking
- **Documentation**: OpenAPI/Swagger
- **Migration**: Flyway for database versioning
- **Build Tool**: Maven
- **Java Version**: 17+

### System Components
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Android App   │    │     Web App     │    │   iOS App       │
└─────────┬───────┘    └─────────┬───────┘    └─────────┬───────┘
          │                      │                      │
          └──────────────────────┼──────────────────────┘
                                 │
                    ┌─────────────┴─────────────┐
                    │   Backend API Server      │
                    │  (Spring Boot + PostgreSQL)│
                    └─────────────┬─────────────┘
                                 │
                    ┌─────────────┴─────────────┐
                    │    PostgreSQL Database     │
                    │         + Redis            │
                    └───────────────────────────┘
```

## 🚀 Features

### Core Functionality
- **User Management**: Multi-role system with RBAC (Customer, Driver, Supervisor, Accountant, Workshop, Admin)
- **Shipment Management**: Complete lifecycle management with real-time tracking
- **Vehicle & Fleet Management**: Comprehensive fleet tracking and maintenance
- **Invoice & Payment**: Automated invoicing with multiple payment methods
- **Real-time Tracking**: WebSocket-based live tracking system
- **Analytics & Reporting**: Advanced business intelligence and custom reports
- **Notifications**: Smart notification system with user preferences
- **Backup & Recovery**: Automated backup and disaster recovery

### Technical Features
- **RESTful API**: Comprehensive REST API with OpenAPI documentation
- **Authentication**: JWT-based authentication with refresh tokens
- **Authorization**: Role-based access control (RBAC) with granular permissions
- **Caching**: Redis-based multi-layer caching strategy
- **Database**: PostgreSQL with advanced indexing and partitioning
- **Monitoring**: Prometheus metrics and Grafana dashboards
- **Security**: Enterprise-grade security with encryption and audit logging
- **Performance**: Optimized for high concurrency and scalability
- **Docker Support**: Complete containerization with Docker Compose

## 📁 Project Structure

```
backend-api/
├── src/
│   ├── main/
│   │   ├── java/com/edham/logistics/backend/
│   │   │   ├── config/           # Security and application configuration
│   │   │   ├── controller/       # REST API controllers
│   │   │   ├── dto/            # Data Transfer Objects
│   │   │   ├── model/          # Entity models
│   │   │   ├── repository/     # Data access layer
│   │   │   ├── service/         # Business logic layer
│   │   │   ├── security/       # Security components
│   │   │   ├── util/           # Utility classes
│   │   │   └── websocket/      # WebSocket handlers
│   │   └── resources/
│   │       ├── application.yml   # Application configuration
│   │       ├── application-prod.yml  # Production config
│   │       ├── application-dev.yml   # Development config
│   │       └── db/migration/   # Database migration scripts
│   └── test/                   # Test classes
├── docs/                       # Documentation
├── docker/                     # Docker configuration
├── pom.xml                     # Maven configuration
└── README.md                    # This file
```

## 🔧 Configuration

### Environment Setup

#### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- PostgreSQL 14 or higher
- Redis 6 or higher

#### Database Configuration
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/edham_logistics
    username: your_username
    password: your_password
    driver-class-name: org.postgresql.Driver
```

#### Redis Configuration
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: your_redis_password
```

### Application Profiles

#### Development (`application-dev.yml`)
- Enabled debug logging
- Hot reload configuration
- Development database settings
- Mock external services

#### Production (`application-prod.yml`)
- Optimized for production
- Security hardening
- Performance tuning
- Production database settings

## 🚀 Getting Started

### 1. Clone the Repository
```bash
git clone <repository-url>
cd backend-api
```

### 2. Database Setup
```bash
# Create PostgreSQL database
createdb edham_logistics

# Run migrations (automatically on startup)
# Or manually using Flyway:
mvn flyway:migrate
```

### 3. Configuration
```bash
# Copy and customize configuration
cp src/main/resources/application-dev.yml.example src/main/resources/application-dev.yml

# Set environment variables
export DATABASE_URL=jdbc:postgresql://localhost:5432/edham_logistics
export DATABASE_USERNAME=your_username
export DATABASE_PASSWORD=your_password
export REDIS_HOST=localhost
export REDIS_PORT=6379
```

### 4. Build and Run
```bash
# Build the application
mvn clean install

# Run the application
mvn spring-boot:run

# Or build and run as JAR
mvn clean package
java -jar target/backend-api-*.jar
```

### 5. Access the Application
- **API Base URL**: http://localhost:8080/api/v1
- **Swagger Documentation**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/actuator/health
- **API Docs**: http://localhost:8080/v3/api-docs

## 📚 API Documentation

### Authentication
All API endpoints require JWT authentication except for:
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/register`
- `POST /api/v1/auth/refresh`

#### Login Request
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

#### Login Response
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "refresh_token_here",
    "user": {
      "id": 1,
      "email": "user@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "role": "CUSTOMER"
    }
  }
}
```

### Core Endpoints

#### Users
- `GET /api/v1/users` - Get all users (Admin only)
- `GET /api/v1/users/{id}` - Get user by ID
- `POST /api/v1/users` - Create user (Admin only)
- `PUT /api/v1/users/{id}` - Update user
- `DELETE /api/v1/users/{id}` - Delete user (Admin only)
- `GET /api/v1/users/profile` - Get current user profile
- `PUT /api/v1/users/profile` - Update current user profile

#### Shipments
- `GET /api/v1/shipments` - Get all shipments
- `GET /api/v1/shipments/{id}` - Get shipment by ID
- `POST /api/v1/shipments` - Create shipment
- `PUT /api/v1/shipments/{id}` - Update shipment
- `DELETE /api/v1/shipments/{id}` - Delete shipment
- `GET /api/v1/shipments/{id}/tracking` - Get shipment tracking
- `POST /api/v1/shipments/{id}/tracking` - Add tracking event
- `PUT /api/v1/shipments/{id}/status` - Update shipment status
- `PUT /api/v1/shipments/{id}/assign` - Assign driver to shipment

#### Invoices
- `GET /api/v1/invoices` - Get all invoices
- `GET /api/v1/invoices/{id}` - Get invoice by ID
- `POST /api/v1/invoices` - Create invoice
- `PUT /api/v1/invoices/{id}` - Update invoice
- `DELETE /api/v1/invoices/{id}` - Delete invoice
- `GET /api/v1/invoices/{id}/pdf` - Get invoice PDF
- `POST /api/v1/invoices/{id}/pay` - Process payment

#### Tracking
- `GET /api/v1/tracking/shipment/{shipmentId}` - Get shipment tracking
- `POST /api/v1/tracking/shipment/{shipmentId}` - Add tracking event
- `POST /api/v1/tracking/driver/location` - Update driver location
- `GET /api/v1/tracking/driver/{driverId}/location` - Get driver location
- `GET /api/v1/tracking/stats` - Get tracking statistics

### Mobile API Endpoints

#### Customer Mobile
- `POST /api/v1/mobile/customer/orders` - Create order
- `GET /api/v1/mobile/customer/orders` - Get customer orders
- `GET /api/v1/mobile/customer/orders/{id}/tracking` - Track order

#### Driver Mobile
- `GET /api/v1/mobile/driver/active-task` - Get active task
- `PUT /api/v1/mobile/driver/location` - Update location
- `PATCH /api/v1/mobile/driver/orders/{id}/status` - Update order status
- `POST /api/v1/mobile/driver/orders/{id}/proof` - Upload delivery proof

## 🔒 Security

### Authentication
- **JWT Tokens**: Secure token-based authentication
- **Refresh Tokens**: Automatic token refresh
- **Role-based Access**: Granular permission control
- **Session Management**: Secure session handling

### Authorization
- **Role-based Access Control (RBAC)**
- **Resource Ownership Verification**
- **API Rate Limiting**
- **CORS Configuration**
- **Input Validation**

### Security Headers
- `X-Content-Type-Options: nosniff`
- `X-Frame-Options: DENY`
- `X-XSS-Protection: 1; mode=block`
- `Strict-Transport-Security: max-age=31536000`

## 📊 Database Schema

### Core Tables
- **users** - User accounts and authentication
- **organizations** - Company/organization data
- **vehicles** - Vehicle fleet management
- **shipments** - Shipment information and status
- **tracking_events** - Shipment tracking history
- **invoices** - Billing and invoicing
- **payments** - Payment transactions
- **user_sessions** - Active user sessions
- **audit_logs** - System audit trail
- **notifications** - User notifications

### Relationships
```
Users (1) → (N) Shipments (as Customer)
Users (1) → (N) Shipments (as Driver)
Users (1) → (N) Vehicles
Vehicles (1) → (N) Shipments
Shipments (1) → (N) Tracking Events
Shipments (1) → (N) Invoices
Invoices (1) → (N) Payments
```

## 🔄 Real-time Features

### WebSocket Endpoints
- `/ws/tracking/{shipmentId}` - Real-time shipment tracking
- `/ws/notifications/{userId}` - User notifications
- `/ws/driver-location/{driverId}` - Driver location updates

### Real-time Events
- Shipment status updates
- Driver location changes
- New notifications
- Payment status changes
- System alerts

## 📈 Analytics & Reporting

### Available Reports
- **Shipment Analytics**: Volume, status distribution, delivery times
- **Revenue Analytics**: Income, payment methods, customer analysis
- **Driver Performance**: Delivery rates, efficiency metrics
- **Customer Analytics**: Order patterns, satisfaction metrics
- **System Performance**: API usage, error rates, response times

### Export Options
- CSV export for all reports
- PDF invoice generation
- Excel export with formatting
- JSON API responses

## 🧪 Testing

### Running Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run with coverage
mvn jacoco:report
```

### Test Coverage
- Unit tests for services and repositories
- Integration tests for controllers
- Security tests for authentication/authorization
- Performance tests for critical endpoints

## 🐳 Docker Deployment

### Build Docker Image
```bash
docker build -t edham-logistics-backend .
```

### Run with Docker Compose
```bash
docker-compose up -d
```

### Docker Compose Configuration
```yaml
version: '3.8'
services:
  backend:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DATABASE_URL=jdbc:postgresql://postgres:5432/edham_logistics
      - DATABASE_USERNAME=postgres
      - DATABASE_PASSWORD=password
    depends_on:
      - postgres
      - redis

  postgres:
    image: postgres:14
    environment:
      - POSTGRES_DB=edham_logistics
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=password
    volumes:
      - postgres_data:/var/lib/postgresql/data

  redis:
    image: redis:6-alpine
    ports:
      - "6379:6379"

volumes:
  postgres_data:
```

## 📊 Monitoring & Logging

### Application Metrics
- **Spring Boot Actuator**: `/actuator/metrics`
- **Health Checks**: `/actuator/health`
- **Performance Metrics**: Response times, throughput
- **Database Metrics**: Connection pool, query performance

### Logging Configuration
```yaml
logging:
  level:
    com.edham.logistics: INFO
    org.springframework.security: WARN
    org.hibernate: WARN
  file:
    name: logs/edham-logistics.log
    max-size: 100MB
    max-history: 30
```

### Health Checks
- Database connectivity
- Redis connectivity
- External service availability
- Disk space monitoring
- Memory usage tracking

## 🔧 Development Tools

### API Documentation
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs
- **Postman Collection**: Available in `docs/` directory

### Database Management
- **Flyway Migrations**: `src/main/resources/db/migration/`
- **Database Schema**: `DATABASE_DESIGN.md`
- **Migration Scripts**: Automated on startup

### Development Utilities
- **H2 Console**: Available in development mode
- **Test Data**: Sample data for testing
- **Debug Configuration**: Enhanced logging for development

## 🚀 Production Deployment

### Production Server Details
- **Host**: 20.199.136.55
- **User**: azureuser
- **Base Directory**: `/home/azureuser/`
- **Deployment Script**: `./deploy_to_azure.sh`

### Manual Deployment Steps
1. **Build and Upload**:
   ```bash
   ./deploy_to_azure.sh
   ```
2. **SSH to Server**:
   ```bash
   ssh azureuser@20.199.136.55
   ```
3. **Run Application**:
   ```bash
   nohup java -jar backend-api-2.0.0.jar > app.log 2>&1 &
   ```

### Environment Variables
```bash
# Database Configuration
DATABASE_URL=jdbc:postgresql://your-db-host:5432/edham_logistics
DATABASE_USERNAME=your_username
DATABASE_PASSWORD=your_password

# Redis Configuration
REDIS_HOST=your-redis-host
REDIS_PORT=6379
REDIS_PASSWORD=your_redis_password

# Security
JWT_SECRET=your_jwt_secret_key
ENCRYPTION_KEY=your_encryption_key

# External Services
STRIPE_SECRET_KEY=your_stripe_key
FCM_SERVER_KEY=your_fcm_key
```

### Production Checklist
- [ ] Environment variables configured
- [ ] Database migrations applied
- [ ] SSL/TLS certificates installed
- [ ] Monitoring and logging configured
- [ ] Backup procedures implemented
- [ ] Security scanning completed
- [ ] Load testing performed
- [ ] Documentation updated

## 📝 API Rate Limiting

### Rate Limits
- **Authentication**: 5 requests per minute
- **General API**: 100 requests per minute
- **Location Updates**: 1000 requests per 15 minutes
- **File Upload**: 10 requests per minute

### Rate Limit Headers
```http
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 95
X-RateLimit-Reset: 1640995200
```

## 🔄 Version Control

### API Versioning
- **Current Version**: v1
- **Version Strategy**: URL path versioning
- **Backward Compatibility**: Maintained for minor versions
- **Deprecation Policy**: 6 months notice for breaking changes

### Migration Strategy
- **Database Migrations**: Flyway version control
- **API Changes**: Versioned endpoints
- **Backward Compatibility**: Maintained where possible
- **Communication**: Change logs and notifications

## 📞 Support & Contact

### Documentation
- **API Documentation**: Available via Swagger UI
- **Database Design**: `DATABASE_DESIGN.md`
- **Architecture**: `BACKEND_ARCHITECTURE.md`

### Support Channels
- **Issues**: GitHub Issues
- **Email**: support@edham-logistics.com
- **Documentation**: docs.edham-logistics.com

### Contributing
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🏆 Acknowledgments

- Spring Boot framework
- PostgreSQL database
- Redis caching
- JWT authentication
- OpenAPI specification
- All contributors and team members

---

**Edham Logistics Backend API** - A comprehensive, scalable solution for modern logistics management.
