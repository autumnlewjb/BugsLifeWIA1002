package com.example.demo.models;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.ArrayList;

import net.bytebuddy.implementation.bind.annotation.FieldValue;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.jboss.logging.annotations.Field;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Indexed
@Table(name = "user")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(allowGetters = true)
public class User implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer user_id;

    @FullTextField
    @Column(nullable=false, updatable=true, unique = true)
    private String email;

    @FullTextField
    @Column(nullable = false, updatable = true, unique = true)
    private String username;
    
    @Column(nullable = false, updatable = true)
    private String password;

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Project> project;
    
    @ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinTable(name = "Users_Roles", joinColumns = {
            @JoinColumn(name = "User_id") }, inverseJoinColumns = {
            @JoinColumn(name = "Role_id") })
    private List<Role> roles=new ArrayList <>();

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

    public int getProjectIndex(Project targetProject){
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
    
}
