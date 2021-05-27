package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.Formula;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Indexed
@Table(name = "issue")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(allowGetters = true)
public class Issue implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer issue_id;

    @FullTextField (analyzer="NAME")
    private String title;

    @GenericField(sortable = Sortable.YES)
    private int priority;

    @KeywordField
    private String status;

    @KeywordField
    @ElementCollection
    @CollectionTable(name = "tag", joinColumns = @JoinColumn(name = "issue_id"))
    private List<String> tag;

    @FullTextField (analyzer="ISSUE")
    @Lob @Basic(fetch = FetchType.LAZY) @Column(columnDefinition = "text", name = "descriptionText")
    private String descriptionText;

    @CreatedBy
    private String createdBy;

    private String assignee;

    @GenericField(sortable = Sortable.YES)
    @Temporal(TemporalType.DATE)
    @CreatedDate
    private Date timestamp;

    @JsonBackReference
    @ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private Project project;

    @IndexedEmbedded
    @JsonManagedReference
    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL)
    private List<Comment> comment;

    @Formula("(select count(*) from Comment c where c.issue_id = issue_id)")
    private Integer commentNum;

    public int getCommentIndex(Comment comment){
        return this.comment.indexOf(comment);
    }

    public Integer getIssue_id() {
        return issue_id;
    }

    public void setIssue_id(Integer issue_id) {
        this.issue_id = issue_id;
    }

    public String getDescriptionText() {
        return descriptionText;
    }

    public void setDescriptionText(String descriptionText) {
        this.descriptionText = descriptionText;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<Comment> getComment() {
        return comment;
    }

    public void setComment(List<Comment> comment) {
        this.comment = comment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(Integer commentNum) {
        this.commentNum = commentNum;
    }
}
