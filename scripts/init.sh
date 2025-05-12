#!/bin/bash

# Build các dịch vụ
echo "Building services..."
./gradlew clean build -x test

# Khởi động Docker Compose
echo "Starting Docker services..."
docker-compose up -d

# Đợi Eureka Server khởi động
echo "Waiting for Eureka Server to start..."
while ! curl -s http://localhost:8761 > /dev/null; do
    echo "Waiting for Eureka Server..."
    sleep 5
done

echo "Eureka Server is up and running!"

# Đợi các dịch vụ đăng ký với Eureka
echo "Waiting for services to register with Eureka..."
sleep 30

echo "System is ready! Access:"
echo "- Frontend: http://localhost"
echo "- API Gateway: http://localhost:8080"
echo "- Eureka Dashboard: http://localhost:8761"