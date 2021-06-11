package com.example.demo.controllers;

import com.example.demo.models.Role;
import com.example.demo.models.User;
import com.example.demo.security.models.AuthenticateRequest;
import com.example.demo.services.LoginService;
import com.example.demo.services.RoleService;
import com.example.demo.services.UserService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final LoginService loginService;
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    public static User referUser;

    @Autowired
    public AuthController(LoginService loginService, UserService userService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.loginService = loginService;
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerPost(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        List<Role> role = new ArrayList<>();
        role.add(roleService.searchRoleByName("USER"));
        user.setRoles(role);
        userService.createUser(user);
        return ResponseEntity.ok(user);
    }
    
    @PostMapping("/register/admin")
    public ResponseEntity<User> registerAdminPost(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        List<Role> role = new ArrayList<>();
        role.add(roleService.searchRoleByName("ADMIN"));
        user.setRoles(role);
        userService.createUser(user);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<User> loginPost(@RequestBody AuthenticateRequest authenticateRequest) {
        loginService.authenticate(authenticateRequest.getUsername(),authenticateRequest.getPassword());
        referUser=userService.getUser(authenticateRequest.getUsername());
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
