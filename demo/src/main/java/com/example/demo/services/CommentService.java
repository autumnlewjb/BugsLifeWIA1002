package com.example.demo.services;

import java.util.List;

import com.example.demo.models.Comment;
import com.example.demo.models.Issue;
import com.example.demo.models.React;
import com.example.demo.repository.CommentRepository;
import javax.transaction.Transactional;

import com.example.demo.repository.IssueRepository;
import com.example.demo.repository.ReactRepository;
import javax.persistence.EntityManager;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CommentService {
    
    @Autowired
    EntityManager entityManager;
    
    private final CommentRepository commentRepository;

    private  final IssueRepository issueRepository;

    private final ReactRepository reactRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, IssueRepository issueRepository, ReactRepository reactRepository) {
        this.commentRepository = commentRepository;
        this.issueRepository = issueRepository;
        this.reactRepository = reactRepository;
    }
    
    public List<Comment> findAllComments() {
        return commentRepository.findAll();
    }
    
    public List<Comment> findCommentsByIssue(Issue issue) {
        return commentRepository.findByIssue(issue);
    }

    public Comment createComments(Comment comment) {
        return commentRepository.save(comment);
    }
    
    public Comment findCommentById(Integer comment_id) {
        return commentRepository.findCommentById(comment_id);
    }

    @PreAuthorize("#comment.user == authentication.name")
    public void deleteComment(Issue issue, Comment comment) {
        issue.getComment().remove(comment);
        comment.setIssue(null);
        commentRepository.delete(comment);
    }

    @PreAuthorize("#comment.user == authentication.name")
    public void updateComment(Integer issue_id, Comment comment, Comment updatedComment) {
        updatedComment.setComment_id(comment.getComment_id());
        List<React> allReactions = reactRepository.findReactionByComment(comment);
        Issue issue = issueRepository.findIssueById(issue_id);
        issue.getComment().set(issue.getCommentIndex(comment), updatedComment);
        for (React react : allReactions){
            react.setComment(updatedComment);
        }
        updatedComment.setIssue(issue);
        updatedComment.setReact(allReactions);
        commentRepository.save(updatedComment);
    }
    
    public List<?> getHistory(Integer comment_id) {
        List<?> commentHistory=AuditReaderFactory.get(entityManager).createQuery().forRevisionsOfEntity(Comment.class, true, true).add(AuditEntity.id().eq(comment_id)).addOrder(AuditEntity.revisionNumber().desc()).getResultList();
        return commentHistory;
    }
    
    public List<?> getOwnHistory(String username) {
        List<?> commentHistory=AuditReaderFactory.get(entityManager).createQuery().forRevisionsOfEntity(Comment.class, true, true).add(AuditEntity.property("user").eq(username)).addOrder(AuditEntity.revisionNumber().desc()).getResultList();
        return commentHistory;
    }
    
    public List<?> getAllHistory() {
        List<?> commentHistory=AuditReaderFactory.get(entityManager).createQuery().forRevisionsOfEntity(Comment.class, true, true).addOrder(AuditEntity.revisionNumber().desc()).getResultList();
        return commentHistory;
    }
}
