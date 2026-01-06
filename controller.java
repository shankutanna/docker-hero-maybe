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
   31  docker run -d --name=web-nginx -p 80:80 nginx:latest
   32  docker ps
   33  docker exec it a4963ac42057 bin/bash
   34  docker exec -it a4963ac42057 bin/bash
   35  docker images
   36  docker rmi nginx:latest
   37  docker rmi -f nginx:latest
   38  docker images
   39  docker ps
   40  docker rm -f a4963ac42057
 #docker rm -f $(docker ps - aq)
 #docker rmi -f $(docker images -aq)
 #docker tag ngnix:latest umatanna9/hello-java:1.0
 #docker push umatanna9/hello-java:1.0
 #docker login
# docker run -d --name=web-baba -p 8000:80 umatanna9/hello-java:1.0




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

