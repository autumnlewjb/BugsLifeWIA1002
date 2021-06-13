package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Formula;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Audited
@Entity
@Indexed
@Table(name = "issue")
@JsonIgnoreProperties(allowGetters = true)
public class Issue implements Serializable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "issue_id")
    private Integer issueId;

    @FullTextField(analyzer = "NAME")
    private String title;

    @GenericField(sortable = Sortable.YES)
    private int priority;

    @KeywordField
    private String status;

    @KeywordField
    @ElementCollection
    @CollectionTable(name = "tag", joinColumns = @JoinColumn(name = "issue_id"))
    private List<String> tag;

    @FullTextField(analyzer = "ISSUE")
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "text", name = "descriptionText")
    private String descriptionText;

    @Column(updatable = false)
    private String createdBy;

    private String assignee;

    private String modifiedBy;

    @GenericField(sortable = Sortable.YES)
    @Column(updatable = false)
    private Timestamp timestamp;

    private Timestamp modifiedDate;

    @JsonBackReference
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    @NotAudited
    private Project project;

    @IndexedEmbedded
    @JsonManagedReference
    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL)
    @NotAudited
    private List<Comment> comment;

    @GenericField(sortable = Sortable.YES)
    @Formula("(select count(*) from comment c where c.issue_id = issue_id)")
    @NotAudited
    private Integer commentNum;

    @JsonManagedReference(value = "issue_attachment")
    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL)
    @NotAudited
    private List<Attachment> attachments;

    public int getCommentIndex(Comment comment) {
        return this.comment.indexOf(comment);
    }

    public Integer getIssueId() {
        return issueId;
    }

    public void setIssueId(Integer issueId) {
        this.issueId = issueId;
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

    public Integer getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(Integer commentNum) {
        this.commentNum = commentNum;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachment) {
        this.attachments = attachment;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Timestamp getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Timestamp modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @JsonProperty
    public Integer getProjectId() {
        return project == null ? null : project.getProjectId();
    }

    @PrePersist
    public void prePersist() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            setCreatedBy(authentication.getName());
            setTimestamp(new Timestamp(Instant.now().toEpochMilli()));
        }
    }

    @PreUpdate
    public void preUpdate() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            setModifiedBy(authentication.getName());
            setModifiedDate(new Timestamp(Instant.now().toEpochMilli()));
        }
    }
}
