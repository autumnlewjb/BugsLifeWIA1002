package com.example.demo.controllers;


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

    @GetMapping("/{comment_id}/reactions")
    public ResponseEntity<List<React>> getReaction(@PathVariable Integer comment_id){
        Comment comment = commentService.findCommentById(comment_id);
        List<React> reactList = reactService.getReactionsByComment(comment);
        return new ResponseEntity<>(reactList, HttpStatus.OK);
    }

    @PostMapping("/{comment_id}/reaction/create")
    public ResponseEntity<React> createReaction(@PathVariable Integer comment_id, @RequestBody React react){
        Comment comment = commentService.findCommentById(comment_id);
        comment.getReact().add(react);
        react.setComment(comment);
        reactService.createReaction(react);
        return new ResponseEntity<>(react, HttpStatus.OK);
    }

    @DeleteMapping("/{comment_id}/{react_id}/deleteReaction")
    public  ResponseEntity<React> deleteReaction(@PathVariable Integer comment_id, @PathVariable Integer react_id){
        Comment comment = commentService.findCommentById(comment_id);
        React react = reactService.findReactionByID(react_id);
        comment.getReact().remove(react);
        react.setComment(null);
        reactService.deleteReaction(react);
        return new ResponseEntity<>(react, HttpStatus.OK);
    }

    @PutMapping("/{comment_id}/{react_id}/updateReaction")
    public ResponseEntity<React> updateReaction(@PathVariable Integer comment_id, @PathVariable Integer react_id, @RequestBody React updatedReact){
        React react = reactService.findReactionByID(react_id);
        updatedReact.setReact_id(react_id);
        reactService.updateReaction(comment_id, react, updatedReact);
        return new ResponseEntity<>(updatedReact, HttpStatus.OK);

    }
}
