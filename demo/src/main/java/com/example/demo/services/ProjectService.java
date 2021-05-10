package com.example.demo.services;

import java.util.List;

import com.example.demo.models.Project;
import com.example.demo.models.User;
import com.example.demo.repository.ProjectRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    UserService userService;

    public List<Project> findAllProjects() {
        return projectRepository.findAll();
    }

    public List<Project> findProjectsWithName(Project project) {
        return projectRepository.findProjectsByName(project.getName());
    }

    public List<Project> findProjectsWithUser(User user) {
        User queryUser = userService.getUser(user.getUsername());
        return userService.getProjectByUser(queryUser);
    }

    public Project createProject(User user, Project project) {
        project.setUser(user);
        return projectRepository.save(project);
    }

}
