package com.example.demo.controllers;

import java.util.List;

import com.example.demo.models.User;
import com.example.demo.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    UserRepository userRepository;

    @GetMapping(path="/users")
    public List<User> getUser() {
        return userRepository.findAll();
    }

    @PostMapping(path="/users")
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }
}
