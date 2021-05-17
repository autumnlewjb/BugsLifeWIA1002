package com.example.demo.controllers;

import java.util.HashMap;

import com.example.demo.repository.UserRepository;
import com.example.demo.security.models.AuthenticateRequest;
import com.example.demo.security.models.AuthenticateResponse;
import com.example.demo.services.LoginService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class WelcomeController {

    HashMap<String, String> db = new HashMap<>();
    @Autowired
    UserRepository userRepository;
    @Autowired
    LoginService loginService;

    @RequestMapping("/")
    public String homepage() {
        return "homepage";
    }

    @GetMapping(path = "/login")
    public String login() {
        return "login";
    }

    @GetMapping(path = "/authenticate")
    public String auth() {
        return "login";
    }

    @PostMapping(path = "/authenticate")
    public ResponseEntity<AuthenticateResponse> loginPost(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            @RequestBody AuthenticateRequest authenticateRequest) throws Exception {
        return loginService.logIn(refreshToken, authenticateRequest);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticateResponse> refreshJwtToken(@CookieValue(name = "refreshToken") String refreshToken) throws Exception {
        return loginService.refreshJwtToken(refreshToken);
    }

    @GetMapping(path = "/logout")
    @ResponseBody
    public String logout() {
        return "logout";
    }

    /*@PostMapping(path = "/login")
    public String loginPost(@RequestParam String username, @RequestParam String password, RedirectAttributes redirectAttributes) {
        if (!loginService.authenticate(username, password)) {
            return "401";
        }
        redirectAttributes.addFlashAttribute("username", username);
        return "redirect:/project-dashboard";
    }*/

    @GetMapping(path = "/register")
    public String register() {
        return "register";
    }

    @PostMapping(path = "/register")
    public String registerPost(@RequestParam String username, @RequestParam String password) {
        System.out.println(username + " " + password);
        db.put(username, password);
        return "redirect:/login";
    }

    @GetMapping(path = "/project-dashboard")
    public String projectDashboard(@ModelAttribute("username") String username, Model model) {
        model.addAttribute("username", username);
        return "project_dashboard";
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
    }

}
