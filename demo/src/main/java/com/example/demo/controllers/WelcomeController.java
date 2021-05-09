package com.example.demo.controllers;

import java.util.HashMap;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class WelcomeController {

    HashMap<String, String> db = new HashMap<>();

    @RequestMapping("/")
    public String homepage() {
        return "homepage";
    }

    @GetMapping(path="/login")
    public String login() {
        return "login";
    }

    @PostMapping(path="/login")
    public String loginPost(@RequestParam String username, @RequestParam String password, RedirectAttributes redirectAttributes) {
        String passwordInDB = db.get(username);
        if (passwordInDB == null || !passwordInDB.equals(password)) {
            return "401";
        }
        redirectAttributes.addFlashAttribute("username", username);
        return "redirect:/project-dashboard";
    }

    @GetMapping(path="/register")
    public String register() {
        return "register";
    }

    @PostMapping(path="/register") 
    public String registerPost(@RequestParam String username, @RequestParam String password) {
        System.out.println(username + " " + password);
        db.put(username, password);
        return "redirect:/login";
    }

    @GetMapping(path="/project-dashboard")
    public String projectDashboard(@ModelAttribute("username") String username, Model model) {
        model.addAttribute("username", username);
        return "project_dashboard";
    }

    @GetMapping(path="/project-page/{projectName}")
    public String projectPage(@PathVariable String projectName, Model model) {
        model.addAttribute("projectName", projectName);
        return "project_page";
    }

    @GetMapping(path="/issues/{projectName}")
    public String issues(@PathVariable String projectName, Model model) {
        model.addAttribute("projectName", projectName);
        return "issue_dashboard";
    }

    @GetMapping(path="/issue-page/{issueName}")
    public String issuePage(@PathVariable String issueName, Model model) {
        model.addAttribute("issueName", issueName);
        return "issue_page";
    }
}
