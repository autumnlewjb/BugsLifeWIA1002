package com.example.demo.services;

import java.util.List;

import com.example.demo.models.Comment;
import com.example.demo.models.Issue;
import com.example.demo.repository.CommentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    
    @Autowired
    CommentRepository commentRepository;

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
