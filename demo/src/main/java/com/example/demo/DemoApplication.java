package com.example.demo;


import com.example.demo.models.Role;
import com.example.demo.models.User;
import com.example.demo.services.RoleService;
import com.example.demo.services.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.dao.DataIntegrityViolationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;

    @Value("${spring.datasource.url}")
    private String database;
    @Value("${spring.datasource.username}")
    private String databaseUsername;
    @Value("${spring.datasource.password}")
    private String databasePassword;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String[] args) throws IOException {
        InputStream inputStream = TypeReference.class.getResourceAsStream("/json/export.json");
        if (roleService.searchRoleByName("ADMIN") == null)
            roleService.createRole(new Role("ADMIN"));
        if (roleService.searchRoleByName("USER") == null)
            roleService.createRole(new Role("USER"));
        if (inputStream == null) {
            if (userService.getUser("CWJ") == null) {
                User CWJ = new User("CWJ@issuetracker.com", "CWJ", "CWJ");
                CWJ.getRoles().add(roleService.searchRoleByName("ADMIN"));
                userService.createUser(CWJ);
            }
            if (userService.getUser("LJB") == null) {
                User LJB = new User("LJB@issuetracker.com", "LJB", "LJB");
                LJB.getRoles().add(roleService.searchRoleByName("ADMIN"));
                userService.createUser(LJB);
            }
            if (userService.getUser("LYM") == null) {
                User LYM = new User("LYM@issuetracker.com", "LYM", "LYM");
                LYM.getRoles().add(roleService.searchRoleByName("ADMIN"));
                userService.createUser(LYM);
            }
            if (userService.getUser("OJS") == null) {
                User OJS = new User("OJS@issuetracker.com", "OJS", "OJS");
                OJS.getRoles().add(roleService.searchRoleByName("ADMIN"));
                userService.createUser(OJS);
            }
        } else {
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<List<User>> typeReference = new TypeReference<List<User>>() {
            };
            try {
                List<User> users = mapper.readValue(inputStream, typeReference);
                for (User user : users) {
                    if (user.getUsername().equals("CWJ") || user.getUsername().equals("LJB") || user.getUsername().equals("LYM") || user.getUsername().equals("OJS"))
                        user.getRoles().add(roleService.searchRoleByName("ADMIN"));
                    else
                        user.getRoles().add(roleService.searchRoleByName("USER"));
                }
                userService.createListOfUsers(users);
                System.out.println("Users Saved!");
            } catch (IOException e) {
                System.out.println("Unable to save users: " + e.getMessage());
            } catch (DataIntegrityViolationException e) {
                System.out.println("Users Saved!");
            }
        }
    }
}
