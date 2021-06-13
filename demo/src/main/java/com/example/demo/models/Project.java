package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.Formula;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "project")
@JsonIgnoreProperties(allowGetters = true)
@Indexed
public class Project implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Integer projectId;

    @FullTextField(analyzer = "NAME")
    @Column(name = "name")
    private String name;

    @FullTextField(analyzer = "PROJECT")
    @Column(columnDefinition = "text", name = "description")
    private String description;

    @GenericField(sortable = Sortable.YES)
    @Column(updatable = false)
    private Timestamp date;

    private Timestamp modifiedDate;

    @JsonBackReference
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @JsonManagedReference
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Issue> issue;

    @GenericField(sortable = Sortable.YES)
    @Formula("(select count(*) from issue i where i.project_id = project_id)")
    private Integer issueNum;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "project_attachment")
    private List<Attachment> attachments;

    public Project() {
    }

    public Project(
            String name, String description, Timestamp date
    ) {
        this.name = name;
        this.description = description;
        this.date = date;
    }

    /*public static Project fromMap(Map<String, String> map) {
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
    }*/

    public int getIssueIndex(Issue issue) {
        return this.issue.indexOf(issue);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        this.user = null;
    }

    public Integer getIssueNum() {
        return issueNum;
    }

    public void setIssueNum(Integer issueNum) {
        this.issueNum = issueNum;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachment) {
        this.attachments = attachment;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Timestamp getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Timestamp modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @PrePersist
    public void prePersist() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            setDate(new Timestamp(Instant.now().toEpochMilli()));
        }
    }

    @PreUpdate
    public void preUpdate() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            setModifiedDate(new Timestamp(Instant.now().toEpochMilli()));
        }
    }
}
