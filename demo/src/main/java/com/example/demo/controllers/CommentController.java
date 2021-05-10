package com.example.demo.controllers;

import java.util.List;

import com.example.demo.models.Comment;
import com.example.demo.models.Issue;
import com.example.demo.services.CommentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommentController {
    
    @Autowired
    CommentService commentService;

    @GetMapping("/comments/issue")
    public List<Comment> getCommentsByIssue(@RequestBody Issue issue) {
        return commentService.findCommentsByIssue(issue);
    }

    @PostMapping("/comments/create")
    public Comment createComments(@RequestBody Comment comment) {
        return commentService.createComments(comment);
    }
}
