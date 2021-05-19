package com.example.demo.controllers;


import com.example.demo.models.Comment;
import com.example.demo.models.React;
import com.example.demo.services.CommentService;
import com.example.demo.services.ReactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ReactController {

    @Autowired
    ReactService reactService;

    @Autowired
    CommentService commentService;

    @GetMapping("/{comment_id}/reactions")
    public List<React> getReaction(@PathVariable Integer comment_id){
        Comment comment = commentService.findCommentById(comment_id);
        return reactService.getReactionsByComment(comment);
    }

    @PostMapping("/{comment_id}/reaction/create")
    public React createReaction(@PathVariable Integer comment_id, @RequestBody React react){
        Comment comment = commentService.findCommentById(comment_id);
        comment.getReact().add(react);
        react.setComment(comment);
        return reactService.createReaction(react);
    }

    @DeleteMapping("/{comment_id}/{react_id}/deleteReaction")
    public void deleteIssue(@PathVariable Integer comment_id, @PathVariable Integer react_id){
        Comment comment = commentService.findCommentById(comment_id);
        React react = reactService.findReactionByID(react_id);
        comment.getReact().remove(react);
        react.setComment(null);
        reactService.deleteReaction(react);
    }
}
