package com.example.demo.services;


import com.example.demo.models.Comment;
import com.example.demo.models.React;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.ReactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.transaction.Transactional;

@Service
@Transactional
public class ReactService {

    private final ReactRepository reactRepository;

    private final CommentRepository commentRepository;

    @Autowired
    public ReactService(ReactRepository reactRepository, CommentRepository commentRepository) {
        this.reactRepository = reactRepository;
        this.commentRepository = commentRepository;
    }

    public List<React> getReactionsByComment(Comment comment){
        return reactRepository.findReactionByComment(comment);
    }

    public React createReaction(React react) {
        return reactRepository.save(react);
    }

    public React findReactionByID(Integer react_id){
        return reactRepository.findReactionByID(react_id);
    }

    @PreAuthorize("#react.reactionBy == authentication.name")
    public void deleteReaction(Comment comment, React react) {
        comment.getReact().remove(react);
        react.setComment(null);
        reactRepository.delete(react);
    }

    @PreAuthorize("#react.reactionBy == authentication.name")
    public void updateReaction(Integer comment_id, React react, React updatedReact) {
        updatedReact.setReact_id(react.getReact_id());
        Comment comment = commentRepository.findCommentById(comment_id);
        comment.getReact().set(comment.getReactionIndex(react), updatedReact);
        updatedReact.setComment(comment);
        reactRepository.save(updatedReact);
    }
}
