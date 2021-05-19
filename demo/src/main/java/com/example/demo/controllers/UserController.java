package com.example.demo.controllers;

import java.util.List;

import com.example.demo.models.User;
import com.example.demo.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    UserService userService;
    
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getUsers();
    }

    @PostMapping("/user/create")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping("/user")
    public User getUser(@RequestBody User user) {
        return userService.getUser(user.getUsername());
    }

    @GetMapping("/user/{username}")
    public User getUser(@PathVariable String username) {
        return userService.getUser(username);
    }

    @DeleteMapping("/{username}")
    public void deleteUser(@PathVariable String username){
        userService.deleteUser(username);
    }


}
