package com.example.demo.controllers;

import java.util.List;

import com.example.demo.models.Comment;
import com.example.demo.models.Issue;
import com.example.demo.services.CommentService;

import com.example.demo.services.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CommentController {
    
    @Autowired
    CommentService commentService;

    @Autowired
    IssueService issueService;

    @GetMapping("/{issue_id}/comments")
    public List<Comment> getCommentsByIssue(@PathVariable Integer issue_id) {
        Issue issue = issueService.findIssuesById(issue_id);
        return commentService.findCommentsByIssue(issue);
    }

    @PostMapping("/{issue_id}/comment/create")
    public Comment createComments(@RequestBody Comment comment, @PathVariable Integer issue_id) {
        Issue issue = issueService.findIssuesById(issue_id);
        issue.getComment().add(comment);
        comment.setIssue(issue);
        return commentService.createComments(comment);
    }
    
    @DeleteMapping("/issue/{issue_id}/delete/comment/{comment_id}")
    public void deleteComment(@PathVariable Integer issue_id, @PathVariable Integer comment_id){
        Issue issue=issueService.findIssuesById(issue_id);
        Comment comment=commentService.findCommentById(comment_id);
        issue.getComment().remove(comment);
        comment.setIssue(null);
        commentService.deleteCommnet(comment);
    }
}
