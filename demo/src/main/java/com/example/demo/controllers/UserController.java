package com.example.demo.controllers;

import com.example.demo.models.User;
import com.example.demo.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {
    @Value("${spring.datasource.url}")
    private String database;
    @Value("${spring.datasource.username}")
    private String databaseUsername;
    @Value("${spring.datasource.password}")
    private String databasePassword;
    private Connection connection = null;
    private PreparedStatement preparedStatement;
    private Statement statement = null;
    private ResultSet userSelect = null;
    private ResultSet projectSelect = null;
    private ResultSet roleSelect = null;
    private ResultSet issueSelect = null;
    private ResultSet commentSelect = null;
    private ResultSet reactSelect = null;
    private ResultSet tagSelect = null;
    private ResultSet issue_audSelect = null;
    private ResultSet comment_audSelect = null;

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> userList = userService.getUsers();
        return ResponseEntity.ok(userList);
    }

    @GetMapping("/user")
    public ResponseEntity<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(userService.getUser(authentication.getName()));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<User> getOtherUser(@PathVariable String username) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getName().equals(username)) {
            return getCurrentUser();
        }
        return ResponseEntity.ok(userService.getUser(username));
    }

    @PutMapping("/user")
    public ResponseEntity<?> updateUser(@RequestBody User updatedUser) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUser(authentication.getName());
        updatedUser.setUser_id(user.getUser_id());
        userService.updateUser(user, updatedUser);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/user/allUsername")
    public ResponseEntity<List<String>> getAllUsername() {
        List<String> usernameList = userService.getAllUsername();
        return ResponseEntity.ok(usernameList);
    }

    @Transactional
    @DeleteMapping("/user")
    public ResponseEntity<?> deleteUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userService.deleteUser(authentication.getName());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/JSON")
    public ResponseEntity<?> getJson() {
        ArrayList<JSONObject> JSONListWithRole = new ArrayList<>();
        ArrayList<JSONObject> JSONListWithoutRole = new ArrayList<>();
        ArrayList<JSONObject> JSONIssueLog=new ArrayList<>();
        ArrayList<JSONObject> JSONCommentLog=new ArrayList<>();
        List<JSONObject> projectList = new ArrayList<>();
        List<JSONObject> issueList = new ArrayList<>();
        List<JSONObject> commentList = new ArrayList<>();
        List<JSONObject> reactList = new ArrayList<>();
        List<JSONObject> tagList = new ArrayList<>();
        try {
            connection = DriverManager.getConnection(database, databaseUsername, databasePassword);
            statement = connection.createStatement();
            preparedStatement = connection.prepareStatement("SELECT * FROM user;");
            userSelect = preparedStatement.executeQuery();
            preparedStatement = connection.prepareStatement("SELECT * FROM project;");
            projectSelect = preparedStatement.executeQuery();
            preparedStatement = connection.prepareStatement("SELECT * FROM users_roles;");
            roleSelect = preparedStatement.executeQuery();
            preparedStatement = connection.prepareStatement("SELECT * FROM issue;");
            issueSelect = preparedStatement.executeQuery();
            preparedStatement = connection.prepareStatement("SELECT * FROM comment;");
            commentSelect = preparedStatement.executeQuery();
            preparedStatement = connection.prepareStatement("SELECT * FROM react;");
            reactSelect = preparedStatement.executeQuery();
            preparedStatement = connection.prepareStatement("SELECT * FROM tag;");
            tagSelect = preparedStatement.executeQuery();
            preparedStatement = connection.prepareStatement("SELECT * FROM issue_aud;");
            issue_audSelect = preparedStatement.executeQuery();
            preparedStatement = connection.prepareStatement("SELECT * FROM comment_aud;");
            comment_audSelect = preparedStatement.executeQuery();
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);

            ResultSetMetaData metaData = userSelect.getMetaData();
            int numOfColumns = metaData.getColumnCount();
            List<String> columnNames = new ArrayList<>();
            for (int i = 1; i <= numOfColumns; i++) {
                columnNames.add(metaData.getColumnName(i));
            }

            metaData = projectSelect.getMetaData();
            int numOfColumns2 = metaData.getColumnCount();
            List<String> columnNames2 = new ArrayList<>();
            for (int i = 1; i <= numOfColumns2; i++) {
                columnNames2.add(metaData.getColumnName(i));
            }

            metaData = issueSelect.getMetaData();
            int numOfColumns4 = metaData.getColumnCount();
            List<String> columnNames4 = new ArrayList<>();
            for (int i = 1; i <= numOfColumns4; i++) {
                columnNames4.add(metaData.getColumnName(i));
            }

            metaData = commentSelect.getMetaData();
            int numOfColumns5 = metaData.getColumnCount();
            List<String> columnNames5 = new ArrayList<>();
            for (int i = 1; i <= numOfColumns5; i++) {
                columnNames5.add(metaData.getColumnName(i));
            }

            metaData = reactSelect.getMetaData();
            int numOfColumns6 = metaData.getColumnCount();
            List<String> columnNames6 = new ArrayList<>();
            for (int i = 1; i <= numOfColumns6; i++) {
                columnNames6.add(metaData.getColumnName(i));
            }

            metaData = tagSelect.getMetaData();
            int numOfColumns7 = metaData.getColumnCount();
            List<String> columnNames7 = new ArrayList<>();
            for (int i = 1; i <= numOfColumns7; i++) {
                columnNames7.add(metaData.getColumnName(i));
            }
            
            metaData = issue_audSelect.getMetaData();
            int numOfColumns8 = metaData.getColumnCount();
            List<String> columnNames8 = new ArrayList<>();
            for (int i = 1; i <= numOfColumns8; i++) {
                columnNames8.add(metaData.getColumnName(i));
            }
            
            metaData = comment_audSelect.getMetaData();
            int numOfColumns9 = metaData.getColumnCount();
            List<String> columnNames9 = new ArrayList<>();
            for (int i = 1; i <= numOfColumns9; i++) {
                columnNames9.add(metaData.getColumnName(i));
            }

            while (projectSelect.next()) {
                JSONObject obj = new JSONObject();
                for (int i = 1; i <= numOfColumns2; i++) {
                    String key = columnNames2.get(i - 1);
                    String value = projectSelect.getString(i);
                    obj.put(key, value);
                }
                projectList.add(obj);
            }

            while (issueSelect.next()) {
                JSONObject obj = new JSONObject();
                for (int i = 1; i <= numOfColumns4; i++) {
                    String key = columnNames4.get(i - 1);
                    if (key.equals("description_text")) {
                        key = "descriptionText";
                    }
                    if (key.equals("created_by")) {
                        key = "createdBy";
                    }
                    if (key.equals("modified_date")) {
                        continue;
                    }
                    if (key.equals("modified_by")) {
                        continue;
                    }
                    String value = issueSelect.getString(i);
                    obj.put(key, value);
                }
                issueList.add(obj);
            }

            while (commentSelect.next()) {
                JSONObject obj = new JSONObject();
                for (int i = 1; i <= numOfColumns5; i++) {
                    String key = columnNames5.get(i - 1);
                    if (key.equals("modified_date")) {
                        continue;
                    }
                    String value = commentSelect.getString(i);
                    obj.put(key, value);
                }
                commentList.add(obj);
            }

            while (reactSelect.next()) {
                JSONObject obj = new JSONObject();
                for (int i = 1; i <= numOfColumns6; i++) {
                    String key = columnNames6.get(i - 1);
                    if (key.equals("reaction_by")) {
                        key = "reactionBy";
                    }
                    String value = reactSelect.getString(i);
                    obj.put(key, value);
                }
                reactList.add(obj);
            }

            while (issue_audSelect.next()) {
                JSONObject obj = new JSONObject();
                for (int i = 1; i <= numOfColumns8; i++) {
                    String key = columnNames8.get(i - 1);
                    String value = issue_audSelect.getString(i);
                    obj.put(key, value);
                }
                JSONIssueLog.add(obj);
            }
            
            while (comment_audSelect.next()) {
                JSONObject obj = new JSONObject();
                for (int i = 1; i <= numOfColumns9; i++) {
                    String key = columnNames9.get(i - 1);
                    String value = comment_audSelect.getString(i);
                    obj.put(key, value);
                }
                JSONCommentLog.add(obj);
            }
            
            while (tagSelect.next()) {
                JSONObject obj = new JSONObject();
                for (int i = 1; i <= numOfColumns7; i++) {
                    String key = columnNames7.get(i - 1);
                    String value = tagSelect.getString(i);
                    obj.put(key, value);
                }
                tagList.add(obj);
            }

            for (int i = 0; i < commentList.size(); i++) {
                List<JSONObject> reacts = new ArrayList<>();
                JSONObject thisComment = commentList.get(i);
                for (int j = 0; j < reactList.size(); j++) {
                    if (Integer.parseInt((String) reactList.get(j).get("comment_id")) == Integer.parseInt((String) commentList.get(i).get("comment_id"))) {
                        JSONObject temp = (JSONObject) reactList.get(j).clone();
                        reactList.get(j).remove("comment_id");
                        reactList.get(j).remove("react_id");
                        reacts.add(reactList.get(j));
                        reactList.set(j, temp);
                    }
                }
                thisComment.put("react", reacts);
            }

            for (int i = 0; i < issueList.size(); i++) {
                List<JSONObject> comments = new ArrayList<>();
                List<String> tags = new ArrayList<>();
                JSONObject thisIssue = issueList.get(i);
                for (int j = 0; j < commentList.size(); j++) {
                    if (Integer.parseInt((String) commentList.get(j).get("issue_id")) == Integer.parseInt((String) issueList.get(i).get("issue_id"))) {
                        JSONObject temp = (JSONObject) commentList.get(j).clone();
                        commentList.get(j).remove("issue_id");
                        commentList.get(j).remove("comment_id");
                        comments.add(commentList.get(j));
                        commentList.set(j, temp);
                    }
                }
                for (int j = 0; j < tagList.size(); j++) {
                    if (Integer.parseInt((String) tagList.get(j).get("issue_id")) == Integer.parseInt((String) issueList.get(i).get("issue_id"))) {
                        JSONObject temp = (JSONObject) tagList.get(j).clone();
                        tagList.get(j).remove("issue_id");
                        tagList.get(j).remove("tag_id");
                        tags.add((String) tagList.get(j).get("tag"));
                        tagList.set(j, temp);
                    }
                }
                thisIssue.put("tag", tags);
                thisIssue.put("comment", comments);
            }

            for (int i = 0; i < projectList.size(); i++) {
                List<JSONObject> issues = new ArrayList<>();
                JSONObject thisProject = projectList.get(i);
                for (int j = 0; j < issueList.size(); j++) {
                    if (Integer.parseInt((String) issueList.get(j).get("project_id")) == Integer.parseInt((String) projectList.get(i).get("project_id"))) {
                        JSONObject temp = (JSONObject) issueList.get(j).clone();
                        issueList.get(j).remove("project_id");
                        issueList.get(j).remove("issue_id");
                        issues.add(issueList.get(j));
                        issueList.set(j, temp);
                    }
                }
                thisProject.put("issue", issues);
            }

            int j = 0;
            while (userSelect.next()) {
                roleSelect.next();
                JSONObject obj = new JSONObject();
                for (int i = 1; i <= numOfColumns; i++) {
                    String key = columnNames.get(i - 1);
                    String value = userSelect.getString(i);
                    obj.put(key, value);
                }
                List<JSONObject> projects = new ArrayList<>();
                for (int i = 0; i < projectList.size(); i++) {
                    if (Integer.parseInt((String) projectList.get(i).get("user_id")) == Integer.parseInt((String) obj.get("user_id"))) {
                        JSONObject temp = (JSONObject) projectList.get(i).clone();
                        projectList.get(i).remove("user_id");
                        projectList.get(i).remove("project_id");
                        projects.add(projectList.get(i));
                        projectList.set(i, temp);
                    }
                }

                JSONObject header = new JSONObject();
                if (Integer.parseInt(roleSelect.getString(2)) == 1) {
                    header.put("role_id", 1);
                    header.put("name", "User");
                } else {
                    header.put("role_id", 2);
                    header.put("name", "Admin");
                }
                obj.put("project", projects);
                obj.put("roles", header);
                obj.remove("user_id");
                JSONListWithRole.add(obj);
                JSONObject temp = (JSONObject) JSONListWithRole.get(j).clone();
                temp.remove("roles");
                JSONListWithoutRole.add(temp);
                j++;
            }
            connection.close();

            try {
                //Two text files and json files for roles and without roles each
                //The JSON file that can be read by our program is without the roles one
                FileWriter file = new FileWriter("log_file.json");
                mapper.writeValue(file, JSONListWithoutRole);
                file.close();
                file = new FileWriter("log_file_role.json");
                mapper.writeValue(file, JSONListWithRole);
                file.close();
                file = new FileWriter("log_file.txt");
                mapper.writeValue(file, JSONListWithoutRole);
                file.close();
                file = new FileWriter("log_file_role.txt");
                mapper.writeValue(file, JSONListWithRole);
                file.close();
                file = new FileWriter("issue_log.json");
                mapper.writeValue(file, JSONIssueLog);
                file.close();
                file = new FileWriter("comment_log.json");
                mapper.writeValue(file, JSONCommentLog);
                file.close();
                return ResponseEntity.ok(JSONListWithRole);
            } catch (IOException e) {
                System.out.println("Error with output file");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Problem with database");
        }
        return null;
    }
}
