package com.example.demo;


import com.example.demo.models.Role;
import com.example.demo.models.User;
import com.example.demo.services.RoleService;
import com.example.demo.services.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;

    @PersistenceContext
    EntityManager entityManager;
    
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
        //Spring will automagically initialize the database with data.sql in resources folder
        //The data imported would not be affected by jpa auditing
        roleService.createRole(new Role("ADMIN"));
        roleService.createRole(new Role("USER"));
        User CWJ=new User("CWJ@issuetracker.com","CWJ","CWJ");
        User LJB=new User("LJB@issuetracker.com","LJB","LJB");
        User LYM=new User("LYM@issuetracker.com","LYM","LYM");
        User OJS=new User("OJS@issuetracker.com","OJS","OJS");
        CWJ.getRoles().add(roleService.searchRoleByName("ADMIN"));
        LJB.getRoles().add(roleService.searchRoleByName("ADMIN"));
        LYM.getRoles().add(roleService.searchRoleByName("ADMIN"));
        OJS.getRoles().add(roleService.searchRoleByName("ADMIN"));
        userService.createUser(CWJ);
        userService.createUser(LJB);
        userService.createUser(LYM);
        userService.createUser(OJS);
        /*
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<User>> typeReference = new TypeReference<List<User>>() {};
        InputStream inputStream = TypeReference.class.getResourceAsStream("/json/data.json");
        try {
            List<User> users = mapper.readValue(inputStream, typeReference);
            for (User user : users) {
                user.getRoles().add(roleService.searchRoleByName("USER"));
            }
            userService.createListOfUsers(users);
            System.out.println("Users Saved!");
        } catch (IOException e) {
            System.out.println("Unable to save users: " + e.getMessage());
        } catch (DataIntegrityViolationException e) {
            System.out.println("Users Saved!");
        }
        */
    }
}
