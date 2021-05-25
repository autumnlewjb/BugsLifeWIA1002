package com.example.demo.controllers;

import com.example.demo.models.User;
import com.example.demo.security.models.AuthenticateRequest;
import com.example.demo.services.LoginService;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final LoginService loginService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(LoginService loginService, UserService userService, PasswordEncoder passwordEncoder) {
        this.loginService = loginService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerPost(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.createUser(user);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<User> loginPost(@RequestBody AuthenticateRequest authenticateRequest) {
        loginService.authenticate(authenticateRequest.getUsername(),authenticateRequest.getPassword());
        return loginService.logIn(authenticateRequest);
    }

    @PostMapping("/logout")
    public String logout() {
        return "logout";
    }

    /*@GetMapping("/")
    public String homepage() {
        return "homepage";
    }

    @GetMapping(path = "/project-dashboard")
    public String projectDashboard(@ModelAttribute("username") String username, Model model) {
        model.addAttribute("username", username);
        return "project_dashboard";
    }

    @GetMapping(path = "/login")
    public String login() {
        return "login";
    }

    @GetMapping(path = "/project-page/{projectName}")
    public String projectPage(@PathVariable String projectName, Model model) {
        model.addAttribute("projectName", projectName);
        return "project_page";
    }

    @GetMapping(path = "/issues/{projectName}")
    public String issues(@PathVariable String projectName, Model model) {
        model.addAttribute("projectName", projectName);
        return "issue_dashboard";
    }

    @GetMapping(path = "/issue-page/{issueName}")
    public String issuePage(@PathVariable String issueName, Model model) {
        model.addAttribute("issueName", issueName);
        return "issue_page";
    }*/

}
