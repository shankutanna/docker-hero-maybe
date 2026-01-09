docker -v
   12  docker images
   13  docker ps
   14  docker pull nginx
   15  docker images ls
   16  docker images
   17  docker run -d --name=web-nginx -p 80:80 nginx:latest
   18  docker ps
   19  docker stop 0a6e259d7a60
   20  docker ps
   21  docker ps -a
   22  docker start 0a6e259d7a60
   23  docker ps
   24  docker rm 0a6e259d7a60
   25  docker stop 0a6e259d7a60
   26  docker rm 0a6e259d7a60
   27  docker ps
   28  docker images
   29  docker rmi nginx:latest
   30  docker images
   31  docker run -d --name=web-nginx -p 80:80 nginx:latest.....for frontend the  default port is 3000:80
   32  docker ps
   33  docker exec it a4963ac42057 bin/bash
   34  docker exec -it a4963ac42057 bin/bash
   35  docker images
   36  docker rmi nginx:latest
   37  docker rmi -f nginx:latest
   38  docker images
   39  docker ps
   40  docker rm -f a4963ac42057
 #docker rm -f $(docker ps -aq)
 #docker rmi -f $(docker images -aq)
 #docker tag ngnix:latest umatanna9/hello-java:1.0
 #docker push umatanna9/hello-java:1.0
 #docker login
# docker run -d --name=web-baba -p 8000:80 umatanna9/hello-java:1.0

# Stop all containers
docker stop $(docker ps -aq)

# Remove all containers
docker rm $(docker ps -aq)

# Remove all images
docker rmi $(docker images -aq)

# Remove unused volumes
docker volume prune -f

# Remove unused networks
docker network prune -f
   

-------------------------------------------------------------------------------------


   # ---------- Stage 1: Build ----------
FROM node:20-alpine AS builder

WORKDIR /app

# Install dependencies
COPY package.json package-lock.json ./
RUN npm ci

# Copy source code
COPY . .

# Build the Vite app
RUN npm run build


# ---------- Stage 2: Serve ----------
FROM nginx:alpine

# Remove default nginx static files
RUN rm -rf /usr/share/nginx/html/*

# Copy built files from builder stage
COPY --from=builder /app/dist /usr/share/nginx/html

# Expose Nginx port
EXPOSE 80

# Start Nginx
CMD ["nginx", "-g", "daemon off;"]

   frontend docker run ...... docker run -d -p 3000:80 --name react-frontend react-frontend:1.
------------------------------------------------------------------------------------------------------------
   
react + springboot + postgreSQL
   
frontend/Dockerfile (React + Nginx) 
# Stage 1: Build React
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Stage 2: Nginx web server
FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
# Optional: SPA fallback rule
# COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

backend/Dockerfile (Spring Boot + PostgreSQL)
   
# Build stage
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
   
---------------------------------------------------------------------------------------------------------------------
docker-compose file
version: "3.8"

services:
  frontend:
    build: ./frontend
    ports:
      - "80:80"
    depends_on:
      - backend
    networks:
      - appnet

  backend:
    build: ./backend
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/mydb
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: example
      SPRING_JPA_HIBERNATE_DDL_AUTO: update
      SPRING_JPA_SHOW_SQL: "true"
    ports:
      - "8080:8080"
    depends_on:
      - postgres
    networks:
      - appnet

  postgres:
    image: postgres:15
    restart: always
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: example
      POSTGRES_DB: mydb
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - appnet

networks:
  appnet:

volumes:
  postgres_data:

------------------------------------------------------------------------------------------------------------------------------------
SINGLE-CONTAINER DEPLOYMENT
   # ================
# Stage 1: Build React
# ================
FROM node:18-alpine AS frontend-build
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ .
RUN npm run build

# ================
# Stage 2: Build Spring Boot JAR
# ================
FROM maven:3.9.4-eclipse-temurin-17 AS backend-build
WORKDIR /app/backend
COPY backend/pom.xml .
COPY backend/src ./src
RUN mvn clean package -DskipTests

# ================
# Stage 3: Combine → Final Image
# ================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Backend
COPY --from=backend-build /app/backend/target/*.jar app.jar

# React static files copied into Spring Boot resources folder
COPY --from=frontend-build /app/frontend/build ./public

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
