package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

@Audited
@Entity
@Indexed
@Table(name = "comment")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(allowGetters = true)
public class Comment implements Cloneable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer comment_id;

    @FullTextField
    @Column(name = "text", nullable = true)
    private String text;

    @JsonManagedReference
    @OneToMany(mappedBy = "comment",  cascade = CascadeType.ALL)
    @NotAudited
    private List<React> react;

    @Column(updatable = false)
    @Temporal(TemporalType.DATE)
    @CreatedDate
    private Date timestamp;

    @Column(updatable = false)
    @CreatedBy
    private String user;

    @JsonBackReference
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "issue_id")
    @Audited
    private Issue issue;

    public int getReactionIndex(React react){
        return this.react.indexOf(react);
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public Integer getComment_id() {
        return comment_id;
    }

    public void setComment_id(Integer comment_id) {
        this.comment_id = comment_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<React> getReact() {
        return react;
    }

    public void setReact(List<React> react) {
        this.react = react;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}
