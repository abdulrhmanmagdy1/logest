#!/bin/bash

# ========================================================
# 🚀 Edham Logistics - Azure Deployment Script
# ========================================================

SERVER_IP="20.199.136.55"
SERVER_USER="azureuser"
JAR_NAME="backend-api-2.0.0.jar"

echo "------------------------------------------------"
echo "📦 1. Building Spring Boot JAR..."
echo "------------------------------------------------"
./mvnw clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ Build failed!"
    exit 1
fi

echo "------------------------------------------------"
echo "📤 2. Uploading JAR to Azure VM ($SERVER_IP)..."
echo "------------------------------------------------"
scp target/$JAR_NAME $SERVER_USER@$SERVER_IP:/home/$SERVER_USER/

if [ $? -ne 0 ]; then
    echo "❌ Upload failed!"
    exit 1
fi

echo "------------------------------------------------"
echo "✅ Success! File uploaded to /home/$SERVER_USER/$JAR_NAME"
echo "------------------------------------------------"
echo "Next steps:"
echo "1. SSH into the server: ssh $SERVER_USER@$SERVER_IP"
echo "2. Run the application: nohup java -jar $JAR_NAME > app.log 2>&1 &"
echo "------------------------------------------------"
