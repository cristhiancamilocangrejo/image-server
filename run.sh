./mvnw clean install

 docker build -f configuration/Dockerfile -t image-server-configuration .

 docker build -f backend-service/Dockerfile -t image-server-app .

 docker-compose up -d

