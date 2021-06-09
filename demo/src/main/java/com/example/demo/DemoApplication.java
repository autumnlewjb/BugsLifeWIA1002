package com.example.demo;


import com.example.demo.models.Role;
import com.example.demo.models.User;
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
        /*
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<User>> typeReference = new TypeReference<List<User>>() {};
        InputStream inputStream = TypeReference.class.getResourceAsStream("/json/data.json");
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
        } catch (DataIntegrityViolationException e) {
            System.out.println("Users Saved!");
        }
        
        /*
            Connection conn = null;
            Statement stmt = null;
            try {
                conn = (Connection) DriverManager.getConnection(database, databaseUsername, databasePassword);
                stmt = (Statement) conn.createStatement();
                String query1 = "DELETE from issue_aud";
                String query2 = "DELETE from comment_aud";
                stmt.executeUpdate(query1);
                System.out.println("Record form issue_aud deleted successfully");
                stmt.executeUpdate(query2);
                System.out.println("Record form comment_aud deleted successfully");
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            JSONParser jsonParser = new JSONParser();
            try {
                JSONArray jsonArray = (JSONArray) jsonParser.parse(new FileReader("C:/Users/ooijs/Desktop/AssignmentExploration/demo/issue_log.json"));
                conn = (Connection) DriverManager.getConnection(database, databaseUsername, databasePassword);
                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO issue_aud values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                for(Object object : jsonArray) {
                     JSONObject record = (JSONObject) object;
                     int issue_id = Integer.parseInt((String) record.get("issue_id"));
                     int rev = Integer.parseInt((String) record.get("rev"));
                     int revtype = Integer.parseInt((String) record.get("revtype"));
                     String assignee = (String) record.get("assignee");
                     String created_by = (String) record.get("created_by");
                     String description_text = (String) record.get("description_text");
                     String modified_by = (String) record.get("modified_by");
                     String date = (String) record.get("modified_date");
                     //Timestamp modified_date = new Timestamp(Date.valueOf(date).getTime());
                     int priority = Integer.parseInt((String) record.get("priority"));
                     String status = (String) record.get("status");
                     date = (String) record.get("timestamp");
                     //Timestamp timestamp = new Timestamp(Date.valueOf(date).getTime());
                     String title = (String) record.get("title");
                     pstmt.setInt(1, issue_id);
                     pstmt.setInt(2, rev);
                     pstmt.setInt(3, revtype);
                     pstmt.setString(4, assignee);
                     pstmt.setString(5, created_by);
                     pstmt.setString(6, description_text);
                     pstmt.setString(7, modified_by);
                     pstmt.setDate(8, null);
                     pstmt.setInt(9, priority);
                     pstmt.setString(10, status);
                     pstmt.setTimestamp(11, null);
                     pstmt.setString(12, title);
                     pstmt.executeUpdate();
                }  
                conn.close();
            System.out.println("Records inserted for issue_aud");
            } catch (FileNotFoundException e) {
               e.printStackTrace();
            } catch (IOException e) {
               e.printStackTrace();
            } catch (Exception e) {
               e.printStackTrace();
            }
            
            try {
                JSONArray jsonArray = (JSONArray) jsonParser.parse(new FileReader("C:/Users/ooijs/Desktop/AssignmentExploration/demo/comment_log.json"));
                conn = (Connection) DriverManager.getConnection(database, databaseUsername, databasePassword);
                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO comment_aud values (?, ?, ?, ?, ?, ?, ?, ? )");
                for(Object object : jsonArray) {
                     JSONObject record = (JSONObject) object;
                     int comment_id = Integer.parseInt((String) record.get("comment_id"));
                     int rev = Integer.parseInt((String) record.get("rev"));
                     int revtype = Integer.parseInt((String) record.get("revtype"));
                     String date = (String) record.get("modified_date");
                     //Timestamp modified_date = new Timestamp(Date.valueOf(date).getTime());
                     String text = (String) record.get("text");
                     date = (String) record.get("timestamp");
                     //Timestamp timestamp = new Timestamp(Date.valueOf(date).getTime());
                     String user = (String) record.get("user");
                     int issue_id = Integer.parseInt((String) record.get("issue_id"));
                     pstmt.setInt(1, comment_id);
                     pstmt.setInt(2, rev);
                     pstmt.setInt(3, revtype);
                     pstmt.setDate(4, null);
                     pstmt.setString(5, text);
                     pstmt.setTimestamp(6, null);
                     pstmt.setString(7, user);
                     pstmt.setInt(8, issue_id);
                     pstmt.executeUpdate();
                }  
                conn.close();
            System.out.println("Records inserted for comment_aud");
            } catch (FileNotFoundException e) {
               e.printStackTrace();
            } catch (IOException e) {
               e.printStackTrace();
            } catch (Exception e) {
               e.printStackTrace();
            }
            */
        }

}
