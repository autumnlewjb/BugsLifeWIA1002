package com.example.demo.services;


import com.example.demo.models.Comment;
import com.example.demo.models.React;
import com.example.demo.repository.ReactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReactService {

    ReactRepository reactRepository;

    @Autowired
    public ReactService(ReactRepository reactRepository) {
        this.reactRepository = reactRepository;
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

    public void deleteReaction(React react) {
        reactRepository.delete(react);
    }
}
