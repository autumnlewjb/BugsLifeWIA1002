package com.example.demo;


import com.example.demo.models.User;
import com.example.demo.services.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    @Autowired
    UserService userService;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);

	}

	@Override
	public void run(String... args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        TypeReference<List<User>> typeReference = new TypeReference<List<User>>(){};
        InputStream inputStream = TypeReference.class.getResourceAsStream("/json/JsonTesting.json");
        try {
            List<User> users = mapper.readValue(inputStream,typeReference);
            userService.createListOfUsers(users);
            System.out.println("Users Saved!");
        } catch (IOException e){
            System.out.println("Unable to save users: " + e.getMessage());
        }
	}
}
