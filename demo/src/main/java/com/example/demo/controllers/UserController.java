package com.example.demo.controllers;

import com.example.demo.models.User;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import javax.transaction.Transactional;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> userList = userService.getUsers();
        return ResponseEntity.ok(userList);
    }

    @GetMapping("/user")
    public ResponseEntity<User> getCurrentUser(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(userService.getUser(authentication.getName()));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<User> getOtherUser(@PathVariable String username){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getName().equals(username)){
            return getCurrentUser();
        }
        return ResponseEntity.ok(userService.getUser(username));
    }

    @PutMapping("/user")
    public ResponseEntity<?> updateUser(@RequestBody User updatedUser){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUser(authentication.getName());
        updatedUser.setUser_id(user.getUser_id());
        userService.updateUser(user, updatedUser);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Transactional
    @DeleteMapping("/user")
    public ResponseEntity<?> deleteUser(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        userService.deleteUser(authentication.getName());
        return ResponseEntity.ok(HttpStatus.OK);
    }

}
