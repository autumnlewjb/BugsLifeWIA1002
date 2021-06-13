#! /bin/bash

curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

sudo curl -L "https://github.com/docker/compose/releases/download/1.29.1/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

sudo chmod +x /usr/local/bin/docker-compose

docker-compose --version

sed -i 's/localhost:3306/sql-server/g' ./demo/src/main/resources/application.properties
sed -i 's|ClassUtils.getDefaultClassLoader().getResource(\"\").getPath() + \"static/\"|\"/var/www/html/\"|g' ./demo/src/main/java/com/example/demo/services/FileStorageService.java
sed -i 's|http://localhost:8080/|/image/|g' ./demo/src/main/java/com/example/demo/services/FileStorageService.java

sudo docker-compose up

