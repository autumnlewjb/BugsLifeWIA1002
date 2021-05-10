package com.example.demo.controllers;

import java.util.List;
import java.util.Map;

import com.example.demo.models.Project;
import com.example.demo.models.User;
import com.example.demo.services.ProjectService;
import com.example.demo.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ProjectController {
    
    @Autowired
    ProjectService projectService;

    @Autowired
    UserService userService;

    @GetMapping("/projects")
    public List<Project> getAllProjects() {
        return projectService.findAllProjects();
    }

    @GetMapping("/project")
    public List<Project> getProjectsWithName(@RequestBody Project project) {
        return projectService.findProjectsWithName(project);
    }
    
    @GetMapping("/project/user")
    public List<Project> getProjectsWithUser(@RequestBody User user) {
        return projectService.findProjectsWithUser(user);
    }

    @PostMapping("/project/create")
    public Project createProject(@RequestBody Map<String, Map<String, String>> payload) {
        User user = userService.getUser(payload.get("user").get("username"));
        Project project = Project.fromMap(payload.get("project"));

        return projectService.createProject(user, project);
    }
}
