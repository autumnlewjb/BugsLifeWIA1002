package com.example.demo.controllers;

import java.util.List;

import com.example.demo.models.Comment;
import com.example.demo.models.Issue;
import com.example.demo.services.CommentService;

import com.example.demo.services.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;
    private final IssueService issueService;

    @Autowired
    public CommentController(CommentService commentService, IssueService issueService) {
        this.commentService = commentService;
        this.issueService = issueService;
    }

    @GetMapping("/{issue_id}/comments")
    public ResponseEntity<List<Comment>> getCommentsByIssue(@PathVariable Integer issue_id) {
        Issue issue = issueService.findIssuesById(issue_id);
        List<Comment> commentList=commentService.findCommentsByIssue(issue);
        return new ResponseEntity<>(commentList, HttpStatus.OK);
    }

    @PostMapping("/{issue_id}/comment/create")
    public ResponseEntity<Comment> createComments(@RequestBody Comment comment, @PathVariable Integer issue_id) {
        Issue issue = issueService.findIssuesById(issue_id);
        issue.getComment().add(comment);
        comment.setIssue(issue);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }
    
    @DeleteMapping("/issue/{issue_id}/delete/comment/{comment_id}")
    public ResponseEntity<Comment> deleteComment(@PathVariable Integer issue_id, @PathVariable Integer comment_id){
        Issue issue=issueService.findIssuesById(issue_id);
        Comment comment=commentService.findCommentById(comment_id);
        issue.getComment().remove(comment);
        comment.setIssue(null);
        commentService.deleteComment(comment);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @PutMapping("comment/{comment_id}/update")
    public ResponseEntity<?> updateComment( @PathVariable Integer comment_id, @RequestBody String text){
        commentService.updateComment(comment_id, text);
        return new ResponseEntity<>(text, HttpStatus.OK);
    }
}
