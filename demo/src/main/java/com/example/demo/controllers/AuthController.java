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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final LoginService loginService;
    private final UserService userService;
    private final RoleService roleService;
    public static User referUser;

    @Autowired
    public AuthController(LoginService loginService, UserService userService, RoleService roleService) {
        this.loginService = loginService;
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<User> registerPost(@RequestBody User user) {
        List<Role> role = new ArrayList<>();
        role.add(roleService.searchRoleByName("USER"));
        user.setRoles(role);
        userService.createUser(user);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/register/admin")
    public ResponseEntity<User> registerAdminPost(@RequestBody User user) {
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

}
