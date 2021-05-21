package com.example.demo;


import com.example.demo.models.Role;
import com.example.demo.models.User;
import com.example.demo.services.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
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
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    @Autowired
    UserService userService;

    @PersistenceContext
    EntityManager entityManager;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    @Transactional(readOnly = false)
    public void run(String[] args) throws IOException, InterruptedException {
        /*ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<User>> typeReference = new TypeReference<List<User>>() {};
        InputStream inputStream = TypeReference.class.getResourceAsStream("/json/JsonTesting.json");
        try {
            Role admin = new Role("ADMIN");
            Role ordinaryUser = new Role("USER");
            List<User> users = mapper.readValue(inputStream, typeReference);
            for (User user : users) {
                if (user.getUsername().equals("admin")) {
                    user.getRoles().add(admin);
                } else {
                    user.getRoles().add(ordinaryUser);
                }
            }
            userService.createListOfUsers(users);
            System.out.println("Users Saved!");
        } catch (IOException e) {
            System.out.println("Unable to save users: " + e.getMessage());
        } catch (DataIntegrityViolationException ex) {
            System.out.println("Users Saved!");
        }*/
        SearchSession searchSession = Search.session( entityManager );
        MassIndexer indexer = searchSession.massIndexer( User.class );
        indexer.startAndWait();
    }
}
