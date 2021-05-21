package com.example.demo.controllers;

import com.example.demo.models.User;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import javax.transaction.Transactional;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> userList = userService.getUsers();
        return new ResponseEntity<>(userList,HttpStatus.OK);
    }

    //Duplicated endpoint
    /*@PostMapping("/user/create")
    public ResponseEntity<?> createUser(@RequestBody User user) throws SQLException {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        try {
            userService.createUser(user);
        } catch (SQLException ex){
            throw new SQLException(ex.getMessage(),ex.getSQLState());
        } catch (Exception ex){
            throw new RuntimeException(ex.getMessage(), ex.getCause());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }*/

    @GetMapping("/user")
    public ResponseEntity<User> getUser(@RequestBody User user){
        return new ResponseEntity<>(userService.getUser(user.getUsername()),HttpStatus.OK);
    }
    
    @Transactional
    @DeleteMapping("/{username}")
    public  ResponseEntity<?> deleteUser(@PathVariable String username){
        userService.deleteUser(username);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{username}/update")
    public ResponseEntity<?> updateUser(@RequestBody User updatedUser, @PathVariable String username){
        User user = userService.getUser(username);
        updatedUser.setUser_id(user.getUser_id());
        userService.updateUser(user, updatedUser);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

}
