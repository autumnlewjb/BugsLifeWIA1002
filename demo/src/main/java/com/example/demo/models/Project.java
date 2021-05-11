package com.example.demo.models;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "project")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(allowGetters = true)
public class Project implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer project_id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;
    
    @Temporal(TemporalType.DATE)
    @CreatedDate
    private Date date;
    
    @JsonBackReference
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @JsonManagedReference
    @OneToMany(mappedBy = "project", cascade=CascadeType.ALL, fetch=FetchType.EAGER, orphanRemoval = true)
    private List<Issue> issue;

    public Project() {}

    public Project(
        String name, String description, Date date
    ) {
        this.name = name;
        this.description = description;
        this.date = date;
    }

    public static Project fromMap(Map<String, String> map) {
        String name = map.get("name");
        String description = map.get("description");
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(map.get("date_created"));

            return new Project(name, description, date);
        } catch (ParseException e) {
            System.out.println("exception");
            return null;
        }
    }
    
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getProject_id() {
        return project_id;
    }
    
    public void setProject_id(Integer project_id) {
        this.project_id = project_id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Date getDate() {
        return date;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Issue> getIssue() {
        return issue;
    }

    public void setIssue(List<Issue> issue) {
        this.issue = issue;
    }
    
    public void removeUser() {
        this.user=null;
    }
}
