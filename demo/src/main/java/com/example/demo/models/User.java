package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

@Entity
@Indexed
@Table(name = "user")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(allowGetters = true)
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer user_id;

    @FullTextField
    @Column(nullable = false, updatable = true, unique = true)
    private String email;

    @FullTextField(analyzer = "NAME")
    @Column(nullable = false, updatable = true, unique = true)
    private String username;

    @Column(nullable = false, updatable = true)
    private String password;

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Project> project;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(name = "Users_Roles", joinColumns = {
            @JoinColumn(name = "User_id")}, inverseJoinColumns = {
            @JoinColumn(name = "Role_id")})
    private List<Role> roles = new ArrayList<>();

    @Transient
    private Stack<Integer> issueIdUndo = new Stack<>();
    @Transient
    private Stack<Integer> commentUndo = new Stack<>();
    @Transient
    private Stack<Integer> issueIdRedo = new Stack<>();
    @Transient
    private Stack<Comment> commentRedo = new Stack<>();
    @Transient
    private HashMap<Integer,HashMap<Integer,Stack<Issue>>> issueUndo = new HashMap<>();
    @Transient
    private HashMap<Integer,HashMap<Integer,Stack<Issue>>> issueRedo = new HashMap<>();

    public User() {
    }

    public User(
            String email, String username, String password
    ) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public static User fromMap(Map<String, String> map) {
        String email = map.get("email");
        String username = map.get("username");
        String password = map.get("password");

        return new User(email, username, password);
    }

    public int getProjectIndex(Project targetProject) {
        return this.project.indexOf(targetProject);
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return this.email;
    }

    public List<Project> getProject() {
        return this.project;
    }

    public void setProject(List<Project> project) {
        this.project = project;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public Stack<Integer> getIssueIdUndo() {
        return issueIdUndo;
    }

    public Stack<Integer> getCommentUndo() {
        return commentUndo;
    }

    public Stack<Integer> getIssueIdRedo() {
        return issueIdRedo;
    }

    public Stack<Comment> getCommentRedo() {
        return commentRedo;
    }

    public HashMap<Integer, HashMap<Integer,Stack<Issue>>> getIssueUndo() {
        return issueUndo;
    }

    public HashMap<Integer, HashMap<Integer,Stack<Issue>>> getIssueRedo() {
        return issueRedo;
    }

}
