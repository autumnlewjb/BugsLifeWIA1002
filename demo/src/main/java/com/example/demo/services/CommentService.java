package com.example.demo.services;

import java.util.List;

import com.example.demo.models.Comment;
import com.example.demo.models.Issue;
import com.example.demo.repository.CommentRepository;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
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
    
    public void deleteCommnet(Comment comment) {
        commentRepository.delete(comment);
    }
}
