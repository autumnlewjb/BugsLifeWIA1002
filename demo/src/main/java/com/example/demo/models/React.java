package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "react")
//@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(allowGetters = true)
public class React {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer react_id;

    @JsonBackReference
    @ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    private String reaction;

    private Integer count;

    @CreatedBy
    private String reactionBy;

    public String getReactionBy() {
        return reactionBy;
    }

    public void setReactionBy(String reactionBy) {
        this.reactionBy = reactionBy;
    }

    public Integer getReact_id() {
        return react_id;
    }

    public void setReact_id(Integer react_id) {
        this.react_id = react_id;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
