package com.example.demo.models;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "user")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(allowGetters = true)
public class User implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @Column(nullable=false, updatable=true)
    private String email;

    @Column(nullable = false, updatable = true)
    private String username;
    
    @Column(nullable = false, updatable = true)
    private String password;

    @JsonManagedReference
    @OneToMany(mappedBy = "user")
    private List<Project> project;

    public User() {}

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

    public Integer getId() {
        return Id;
    }
    public void setId(Integer id) {
        Id = id;
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
}
