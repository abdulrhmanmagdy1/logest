# Edham Logistics Backend - Deployment Guide

## 📋 Overview

This comprehensive guide covers deployment of the Edham Logistics backend API system in various environments, from development to production.

## 🏗️ Architecture Overview

### System Components
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Load Balancer │    │   Application   │    │   Database      │
│     (Nginx)     │───▶│   (Spring Boot) │───▶│   (PostgreSQL)  │
│   Port: 80/443  │    │   Port: 8080   │    │   Port: 5432    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
                    ┌─────────────┴─────────────┐
                    │      Redis Cache         │
                    │       Port: 6379         │
                    └───────────────────────────┘
```

## 🚀 Prerequisites

### System Requirements

#### Minimum Requirements
- **CPU**: 2 cores
- **Memory**: 4GB RAM
- **Storage**: 20GB SSD
- **OS**: Linux (Ubuntu 20.04+ recommended)

#### Recommended Production Requirements
- **CPU**: 4+ cores
- **Memory**: 8GB+ RAM
- **Storage**: 100GB+ SSD
- **OS**: Linux (Ubuntu 22.04+ recommended)
- **Network**: 1Gbps+ connection

### Software Dependencies

#### Required Software
- **Java**: OpenJDK 17 or higher
- **Maven**: 3.6.0 or higher
- **PostgreSQL**: 14.0 or higher
- **Redis**: 6.0 or higher
- **Nginx**: 1.18 or higher (for production)
- **Docker**: 20.10+ (optional, for containerized deployment)
- **Docker Compose**: 2.0+ (optional)

#### External Services
- **Email Service**: SMTP server (Gmail, SendGrid, etc.)
- **SMS Service**: Twilio or similar
- **Push Notification**: Firebase Cloud Messaging
- **Payment Gateway**: Stripe or similar
- **File Storage**: AWS S3 or local storage

## 🔧 Environment Setup

### 1. Development Environment

#### Local Setup
```bash
# Clone repository
git clone <repository-url>
cd backend-api

# Set up environment variables
cp .env.example .env
# Edit .env with your configuration

# Install dependencies
mvn clean install

# Run database migrations
mvn flyway:migrate

# Start application
mvn spring-boot:run
```

#### Docker Development Setup
```bash
# Using Docker Compose for development
docker-compose -f docker-compose.yml --profile development up -d

# View logs
docker-compose logs -f backend

# Stop services
docker-compose down
```

### 2. Staging Environment

#### Semi-Production Setup
```bash
# Build application
mvn clean package -Pprod

# Set production environment variables
export SPRING_PROFILES_ACTIVE=prod
export DATABASE_URL=jdbc:postgresql://staging-db:5432/edham_logistics_staging
export DATABASE_USERNAME=staging_user
export DATABASE_PASSWORD=staging_password

# Run application
java -jar target/edham-logistics-backend.jar
```

### 3. Production Environment

#### Containerized Deployment (Recommended)
```bash
# Build Docker image
docker build -t edham-logistics-backend:2.0.0 .

# Tag for production
docker tag edham-logistics-backend:2.0.0 registry.example.com/edham-logistics-backend:2.0.0

# Push to registry
docker push registry.example.com/edham-logistics-backend:2.0.0

# Deploy with Docker Compose
docker-compose -f docker-compose.yml --profile production up -d
```

## 📦 Deployment Methods

### Method 1: Traditional Server Deployment

#### Step 1: Server Preparation
```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Install Java 17
sudo apt install openjdk-17-jdk -y

# Install Maven
sudo apt install maven -y

# Install PostgreSQL
sudo apt install postgresql postgresql-contrib -y

# Install Redis
sudo apt install redis-server -y

# Install Nginx
sudo apt install nginx -y
```

#### Step 2: Database Setup
```bash
# Create database
sudo -u postgres createdb edham_logistics

# Create user
sudo -u postgres createuser --interactive edham_user

# Grant privileges
sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE edham_logistics TO edham_user;"
```

#### Step 3: Application Deployment
```bash
# Create application directory
sudo mkdir -p /opt/edham-logistics
sudo chown $USER:$USER /opt/edham-logistics
cd /opt/edham-logistics

# Clone and build
git clone <repository-url> .
mvn clean package -Pprod

# Create systemd service
sudo tee /etc/systemd/system/edham-backend.service > /dev/null <<EOF
[Unit]
Description=Edham Logistics Backend
After=network.target

[Service]
Type=simple
User=edham
WorkingDirectory=/opt/edham-logistics
ExecStart=/usr/bin/java -jar target/edham-logistics-backend.jar
Restart=always
RestartSec=10
Environment=SPRING_PROFILES_ACTIVE=prod

[Install]
WantedBy=multi-user.target
EOF

# Enable and start service
sudo systemctl enable edham-backend
sudo systemctl start edham-backend
```

#### Step 4: Nginx Configuration
```bash
# Copy Nginx configuration
sudo cp docker/nginx/nginx.conf /etc/nginx/sites-available/edham-logistics

# Enable site
sudo ln -s /etc/nginx/sites-available/edham-logistics /etc/nginx/sites-enabled/
sudo rm /etc/nginx/sites-enabled/default

# Test and reload Nginx
sudo nginx -t
sudo systemctl reload nginx
```

### Method 2: Docker Container Deployment

#### Step 1: Docker Setup
```bash
# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Add user to docker group
sudo usermod -aG docker $USER
```

#### Step 2: Environment Configuration
```bash
# Create production environment file
cp .env.example .env.production

# Edit production values
nano .env.production
```

#### Step 3: Deployment
```bash
# Deploy with Docker Compose
docker-compose --env-file .env.production up -d

# Scale application if needed
docker-compose --env-file .env.production up -d --scale backend=3
```

### Method 3: Kubernetes Deployment

#### Step 1: Kubernetes Setup
```yaml
# k8s/namespace.yaml
apiVersion: v1
kind: Namespace
metadata:
  name: edham-logistics
---
# k8s/configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: edham-config
  namespace: edham-logistics
data:
  SPRING_PROFILES_ACTIVE: "prod"
  DATABASE_HOST: "postgres-service"
  REDIS_HOST: "redis-service"
```

#### Step 2: Application Deployment
```yaml
# k8s/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: edham-backend
  namespace: edham-logistics
spec:
  replicas: 3
  selector:
    matchLabels:
      app: edham-backend
  template:
    metadata:
      labels:
        app: edham-backend
    spec:
      containers:
      - name: backend
        image: edham-logistics/backend:2.0.0
        ports:
        - containerPort: 8080
        envFrom:
        - configMapRef:
            name: edham-config
        - secretRef:
            name: edham-secrets
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
```

#### Step 3: Service and Ingress
```yaml
# k8s/service.yaml
apiVersion: v1
kind: Service
metadata:
  name: edham-backend-service
  namespace: edham-logistics
spec:
  selector:
    app: edham-backend
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
  type: ClusterIP
---
# k8s/ingress.yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: edham-backend-ingress
  namespace: edham-logistics
  annotations:
    kubernetes.io/ingress.class: nginx
    cert-manager.io/cluster-issuer: letsencrypt-prod
spec:
  tls:
  - hosts:
    - api.edham-logistics.com
    secretName: edham-tls
  rules:
  - host: api.edham-logistics.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: edham-backend-service
            port:
              number: 80
```

## 🔒 Security Configuration

### SSL/TLS Setup

#### Let's Encrypt with Nginx
```bash
# Install Certbot
sudo apt install certbot python3-certbot-nginx -y

# Obtain SSL certificate
sudo certbot --nginx -d api.edham-logistics.com -d www.api.edham-logistics.com

# Auto-renewal
sudo crontab -e
# Add: 0 12 * * * /usr/bin/certbot renew --quiet
```

#### Manual SSL Configuration
```bash
# Generate self-signed certificate (for development)
sudo openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
    -keyout /etc/ssl/private/edham.key \
    -out /etc/ssl/certs/edham.crt \
    -subj "/C=SA/ST=Riyadh/L=Edham/O=Edham Logistics/CN=api.edham-logistics.com"
```

### Firewall Configuration
```bash
# Configure UFW firewall
sudo ufw enable
sudo ufw allow ssh
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw allow 5432/tcp  # PostgreSQL (only if internal access needed)
sudo ufw allow 6379/tcp  # Redis (only if internal access needed)
```

## 📊 Monitoring Setup

### Prometheus and Grafana
```bash
# Deploy monitoring stack
docker-compose --profile monitoring up -d

# Access Grafana
# URL: http://your-server:3000
# Username: admin
# Password: admin123 (change immediately)
```

### Log Management
```bash
# Configure log rotation
sudo tee /etc/logrotate.d/edham-logistics > /dev/null <<EOF
/opt/edham-logistics/logs/*.log {
    daily
    missingok
    rotate 30
    compress
    delaycompress
    notifempty
    create 644 edham edham
    postrotate
        systemctl reload edham-backend
    endscript
}
EOF
```

## 🔄 CI/CD Pipeline

### GitHub Actions Configuration
```yaml
# .github/workflows/deploy.yml
name: Deploy to Production

on:
  push:
    branches: [ main ]
    tags: [ 'v*' ]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    - name: Run tests
      run: mvn test
    - name: Run integration tests
      run: mvn verify

  build-and-deploy:
    needs: test
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    steps:
    - uses: actions/checkout@v3
    - name: Set up Docker Buildx
      uses: docker/setup-buildx-action@v2
    - name: Login to Container Registry
      uses: docker/login-action@v2
      with:
        registry: ${{ secrets.REGISTRY_URL }}
        username: ${{ secrets.REGISTRY_USERNAME }}
        password: ${{ secrets.REGISTRY_PASSWORD }}
    - name: Build and push Docker image
      uses: docker/build-push-action@v4
      with:
        context: .
        push: true
        tags: ${{ secrets.REGISTRY_URL }}/edham-logistics-backend:${{ github.sha }}
    - name: Deploy to production
      uses: appleboy/ssh-action@v0.1.5
      with:
        host: ${{ secrets.PROD_HOST }}
        username: ${{ secrets.PROD_USERNAME }}
        key: ${{ secrets.PROD_SSH_KEY }}
        script: |
          cd /opt/edham-logistics
          docker-compose pull
          docker-compose up -d
```

## 📋 Environment Variables

### Production Variables
```bash
# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/edham_logistics
DATABASE_USERNAME=edham_user
DATABASE_PASSWORD=your_secure_password

# Security
JWT_SECRET=your_256_bit_jwt_secret_key_here
ENCRYPTION_KEY=your_32_bit_encryption_key

# External Services
STRIPE_SECRET_KEY=sk_live_your_stripe_secret_key
FCM_SERVER_KEY=your_firebase_server_key
TWILIO_ACCOUNT_SID=your_twilio_account_sid
TWILIO_AUTH_TOKEN=your_twilio_auth_token

# Email
EMAIL_HOST=smtp.your-domain.com
EMAIL_PORT=587
EMAIL_USERNAME=noreply@your-domain.com
EMAIL_PASSWORD=your_email_password

# Storage
STORAGE_TYPE=s3
AWS_ACCESS_KEY=your_aws_access_key
AWS_SECRET_KEY=your_aws_secret_key
AWS_REGION=us-east-1
S3_BUCKET=edham-logistics-files
```

## 🚀 Performance Optimization

### Database Optimization
```sql
-- PostgreSQL configuration optimizations
-- Add to postgresql.conf

# Memory settings
shared_buffers = 256MB
effective_cache_size = 1GB
work_mem = 4MB
maintenance_work_mem = 64MB

# Connection settings
max_connections = 200
shared_preload_libraries = 'pg_stat_statements'

# Query optimization
random_page_cost = 1.1
effective_io_concurrency = 200
```

### Application Optimization
```yaml
# application-prod.yml optimizations
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
  jpa:
    properties:
      hibernate:
        jdbc:
          batch_size: 50
          order_inserts: true
          order_updates: true
        cache:
          use_second_level_cache: true
          use_query_cache: true
```

## 🔧 Maintenance Procedures

### Database Maintenance
```bash
# Create maintenance script
cat > /opt/edham-logistics/scripts/maintenance.sh << 'EOF'
#!/bin/bash

# Database backup
pg_dump -h localhost -U edham_user edham_logistics | gzip > /backups/edham-logistics-$(date +%Y%m%d).sql.gz

# Database cleanup
psql -h localhost -U edham_user -d edham_logistics -c "SELECT cleanup_old_data(90);"

# Update statistics
psql -h localhost -U edham_user -d edham_logistics -c "ANALYZE;"

# Reindex
psql -h localhost -U edham_user -d edham_logistics -c "REINDEX DATABASE edham_logistics;"
EOF

chmod +x /opt/edham-logistics/scripts/maintenance.sh

# Schedule daily maintenance
crontab -e
# Add: 0 2 * * * /opt/edham-logistics/scripts/maintenance.sh
```

### Application Updates
```bash
# Create update script
cat > /opt/edham-logistics/scripts/update.sh << 'EOF'
#!/bin/bash

# Backup current version
cp target/edham-logistics-backend.jar target/edham-logistics-backend.jar.backup

# Pull latest code
git pull origin main

# Build new version
mvn clean package -Pprod

# Restart application
systemctl restart edham-backend

# Wait for startup
sleep 30

# Health check
curl -f http://localhost:8080/actuator/health || {
    echo "Health check failed, rolling back..."
    cp target/edham-logistics-backend.jar.backup target/edham-logistics-backend.jar
    systemctl restart edham-backend
}
EOF

chmod +x /opt/edham-logistics/scripts/update.sh
```

## 🚨 Troubleshooting

### Common Issues

#### Application Won't Start
```bash
# Check logs
sudo journalctl -u edham-backend -f

# Check Java version
java -version

# Check port availability
netstat -tulpn | grep :8080

# Check file permissions
ls -la /opt/edham-logistics/
```

#### Database Connection Issues
```bash
# Test database connection
psql -h localhost -U edham_user -d edham_logistics -c "SELECT 1;"

# Check PostgreSQL status
sudo systemctl status postgresql

# Check PostgreSQL logs
sudo tail -f /var/log/postgresql/postgresql-14-main.log
```

#### Performance Issues
```bash
# Check system resources
top
htop
iostat -x 1

# Check application metrics
curl http://localhost:8080/actuator/metrics

# Check database queries
psql -h localhost -U edham_user -d edham_logistics -c "SELECT * FROM pg_stat_activity;"
```

### Health Checks
```bash
# Application health
curl http://localhost:8080/actuator/health

# Database health
curl http://localhost:8080/actuator/health/db

# Redis health
redis-cli ping

# Nginx status
sudo systemctl status nginx
```

## 📊 Monitoring Dashboards

### Key Metrics to Monitor

#### Application Metrics
- HTTP Request Rate
- Response Time (p95, p99)
- Error Rate (4xx, 5xx)
- JVM Memory Usage
- CPU Usage
- Database Connection Pool Usage

#### Business Metrics
- Shipment Creation Rate
- Delivery Success Rate
- Revenue Generation
- User Registration Rate
- Payment Success Rate

#### Infrastructure Metrics
- Server CPU/Memory/Disk Usage
- Database Performance
- Redis Memory Usage
- Network Latency
- SSL Certificate Expiry

## 🔄 Backup and Recovery

### Database Backup Strategy
```bash
# Full daily backup
0 2 * * * pg_dump -h localhost -U edham_user edham_logistics | gzip > /backups/daily/edham-logistics-$(date +\%Y\%m\%d).sql.gz

# Incremental hourly backup
0 * * * * pg_dump -h localhost -U edham_user --format=custom --file=/backups/incremental/edham-logistics-$(date +\%Y\%m\%d_\%H).dump edham_logistics

# Point-in-time recovery setup
archive_mode = on
archive_command = 'cp %p /backups/wal/%f'
```

### Application Backup
```bash
# Backup application files
tar -czf /backups/app/edham-logistics-app-$(date +%Y%m%d).tar.gz \
    /opt/edham-logistics/ \
    --exclude=/opt/edham-logistics/logs \
    --exclude=/opt/edham-logistics/target

# Backup configuration files
tar -czf /backups/config/edham-logistics-config-$(date +%Y%m%d).tar.gz \
    /etc/nginx/sites-available/edham-logistics \
    /etc/systemd/system/edham-backend.service \
    /opt/edham-logistics/.env
```

### Recovery Procedures
```bash
# Database recovery
gunzip -c /backups/daily/edham-logistics-20231201.sql.gz | psql -h localhost -U edham_user edham_logistics

# Application recovery
tar -xzf /backups/app/edham-logistics-app-20231201.tar.gz -C /
systemctl restart edham-backend
```

## 📞 Support and Contacts

### Emergency Contacts
- **Development Team**: dev@edham-logistics.com
- **Infrastructure Team**: infra@edham-logistics.com
- **Security Team**: security@edham-logistics.com

### Documentation
- **API Documentation**: https://api.edham-logistics.com/swagger-ui.html
- **Monitoring**: https://monitoring.edham-logistics.com
- **Status Page**: https://status.edham-logistics.com

### Support Channels
- **GitHub Issues**: https://github.com/edham-logistics/backend-api/issues
- **Slack**: #edham-logistics-support
- **Email**: support@edham-logistics.com

---

This deployment guide provides comprehensive instructions for deploying the Edham Logistics backend in various environments. For specific issues or questions, please contact the support team.
