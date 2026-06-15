@echo off
title Edham Logistics Server Publisher

echo Starting Server Deployment Process...

echo Building Spring Boot JAR...
call mvnw.cmd clean package -DskipTests

echo Building Docker Images and Starting Services...
docker-compose up --build -d

echo ✅ Server is now published and running at http://localhost:8080
echo 🔐 Database (PostgreSQL) is running on port 5432
echo ⚡ Redis Cache is running on port 6379
echo 📊 Monitoring (Grafana) is available on port 3000

echo ------------------------------------------------
echo To view logs, use: docker-compose logs -f backend
echo To stop server, use: docker-compose down
echo ------------------------------------------------
pause
