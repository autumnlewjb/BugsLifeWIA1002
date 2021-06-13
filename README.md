# AssignmentExploration

## Requirements
- Java Development Kit (JDK) 11
- npm
- Maven
- MySQL server

## Development Guide
### Start database server locally
Start your mysql server with the credentials below  
```user: root, password: secret```  

*if you would like to use a different user and password, specify in the file ```src\main\resources\application.properties```*

### Start the Spring backend service
You have two options here:  


Start with terminal  
1. Change the terminal directory to the ```demo``` directory from the project root
2. Run ```./mvnw spring-boot:run``` in Shell ```mnvw.cmd spring-boot:run``` in windows DOS command prompt, usually on port ```8080```  

Start with your preferred IDE
1. Import the ```./demo``` directory in your preferred IDE
2. Run the project with the main class in ```src/main/java/com/example/demo/DemoApplication.java```, usually on port ```8080```

### Start the frontend app
You have two options here:  


Start with terminal
1. Change the terminal directory to the ```frontend``` directory from the project root 
2. Run ```npm install``` and wait for completion
3. Run ```npm run serve``` to start the development server locally, usually on port ```3000```

Start with your preferred IDE
1. Open the ```frontend``` directory in your preferred IDE
2. Open up a terminal in the IDE
3. Run ```npm install``` and wait for completion
4. Run ```npm run serve``` to start the development server locally, usually on port ```3000```


## Deployment Guide
In order to follow this guide you need to be on a Linux environment. 
### Edit database settings
You can edit the database settings in the ```deploy-compose.yml``` file and make sure all configurations are the same as the spring database configuration in ```./demo/src/main/resources/application.properties```. 
### Start all services with deployment script
1. Change directory to the root directory of this project.
2. Change permission to execute the deployment script ```deploy.sh```  
3. Run the script with the command ```./deploy.sh```  

*The deployment script will install docker and docker compose as well as starting all the services*
