package com.example.demo.controllers;

import java.util.List;

import com.example.demo.exception.ResourceNotFoundException;
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

    @GetMapping("/{project_id}/{issue_id}")
    public ResponseEntity<List<Comment>> getCommentsByIssue(@PathVariable Integer issue_id) {
        Issue issue = issueService.findIssuesById(issue_id);
        if (issue == null) {
            throw new ResourceNotFoundException("issue", "id", issue_id);
        }
        List<Comment> commentList=commentService.findCommentsByIssue(issue);
        return ResponseEntity.ok(commentList);
    }

    @PostMapping("{project_id}/{issue_id}")
    public ResponseEntity<Comment> createComments(@PathVariable Integer issue_id, @RequestBody Comment comment) {
        Issue issue = issueService.findIssuesById(issue_id);
        if (issue == null) {
            throw new ResourceNotFoundException("issue", "id", issue_id);
        }
        issue.getComment().add(comment);
        comment.setIssue(issue);
        commentService.createComments(comment);
        return ResponseEntity.ok(comment);
    }

    @PutMapping("{project_id}/{issue_id}/{comment_id}")
    public ResponseEntity<?> updateComment( @PathVariable Integer issue_id, @PathVariable Integer comment_id, @RequestBody Comment updatedComment){
        Comment comment = commentService.findCommentById(comment_id);
        if (comment == null) {
            throw new ResourceNotFoundException("comment", "id", comment_id);
        }
        commentService.updateComment(issue_id, comment, updatedComment);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("{project_id}/{issue_id}/{comment_id}")
    public ResponseEntity<?> deleteComment(@PathVariable Integer issue_id, @PathVariable Integer comment_id){
        Issue issue=issueService.findIssuesById(issue_id);
        if (issue == null) {
            throw new ResourceNotFoundException("issue", "id", issue_id);
        }
        Comment comment=commentService.findCommentById(comment_id);
        if (comment == null) {
            throw new ResourceNotFoundException("comment", "id", comment_id);
        }
        commentService.deleteComment(issue, comment);
        return ResponseEntity.ok(HttpStatus.OK);
    }

}
