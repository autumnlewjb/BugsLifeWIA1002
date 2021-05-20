package com.example.demo.services;

import java.util.List;

import com.example.demo.models.Comment;
import com.example.demo.models.Issue;
import com.example.demo.models.React;
import com.example.demo.repository.CommentRepository;
import javax.transaction.Transactional;

import com.example.demo.repository.IssueRepository;
import com.example.demo.repository.ReactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;

    private  final IssueRepository issueRepository;

    private final ReactRepository reactRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, IssueRepository issueRepository, ReactRepository reactRepository) {
        this.commentRepository = commentRepository;
        this.issueRepository = issueRepository;
        this.reactRepository = reactRepository;
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
    
    public void deleteComment(Comment comment) {
        commentRepository.delete(comment);
    }


    public void updateComment(Integer issue_id, Comment comment, Comment updatedComment) {
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
}
