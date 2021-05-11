package com.example.demo.controllers;

import java.util.List;

import com.example.demo.models.Project;
import com.example.demo.models.User;
import com.example.demo.services.ProjectService;
import com.example.demo.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    
    @GetMapping("/{username}/projects")
    public List<Project> getProjectsWithUser(@PathVariable String username) {
        return projectService.findProjectsWithUser(username);
    }

    @PostMapping("/project/create")
    public Project createProject(@RequestBody Project project) {
        return projectService.createProject(project);
    }

    @PostMapping("{userId}/project/create")
    public Project createProjectWithUserId(@PathVariable Integer userId, @RequestBody Project project) {
        // find user based on userId
        User user = userService.getUserById(userId);
        // relate user to project (set project.user = user object)
        project.setUser(user);
        user.getProject().add(project);
        // save project
        return projectService.createProject(project);
    }
}
