package com.example.demo.controllers;


import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.models.Comment;
import com.example.demo.models.React;
import com.example.demo.services.CommentService;
import com.example.demo.services.ReactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ReactController {

    private final ReactService reactService;

    private final CommentService commentService;

    @Autowired
    public ReactController(ReactService reactService, CommentService commentService) {
        this.reactService = reactService;
        this.commentService = commentService;
    }

    @GetMapping("/{project_id}/{issue_id}/{comment_id}")
    public ResponseEntity<List<React>> getReaction(@PathVariable Integer comment_id){
        Comment comment = commentService.findCommentById(comment_id);
        if (comment == null) {
            throw new ResourceNotFoundException("comment", "id", comment_id);
        }
        List<React> reactList = reactService.getReactionsByComment(comment);
        return ResponseEntity.ok(reactList);
    }

    @PostMapping("/{project_id}/{issue_id}/{comment_id}")
    public ResponseEntity<React> createReaction(@PathVariable Integer comment_id, @RequestBody React react){
        Comment comment = commentService.findCommentById(comment_id);
        if (comment == null) {
            throw new ResourceNotFoundException("comment", "id", comment_id);
        }
        comment.getReact().add(react);
        react.setComment(comment);
        reactService.createReaction(react);
        return ResponseEntity.ok(react);
    }

    @PutMapping("/{project_id}/{issue_id}/{comment_id}/{react_id}")
    public ResponseEntity<?> updateReaction(@PathVariable Integer comment_id, @PathVariable Integer react_id, @RequestBody React updatedReact){
        React react = reactService.findReactionByID(react_id);
        if (react == null) {
            throw new ResourceNotFoundException("react", "id", react_id);
        }
        reactService.updateReaction(comment_id, react, updatedReact);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{project_id}/{issue_id}/{comment_id}/{react_id}")
    public  ResponseEntity<?> deleteReaction(@PathVariable Integer comment_id, @PathVariable Integer react_id){
        Comment comment = commentService.findCommentById(comment_id);
        if (comment == null) {
            throw new ResourceNotFoundException("comment", "id", comment_id);
        }
        React react = reactService.findReactionByID(react_id);
        if (react == null) {
            throw new ResourceNotFoundException("react", "id", react_id);
        }
        reactService.deleteReaction(comment, react);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
