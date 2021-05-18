package com.example.demo.services;

import java.util.List;

import com.example.demo.models.Project;
import com.example.demo.models.User;
import com.example.demo.repository.ProjectRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserService userService;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, UserService userService) {
        this.projectRepository = projectRepository;
        this.userService = userService;
    }

    public List<Project> findAllProjects() {
        return projectRepository.findAll();
    }

    public List<Project> findProjectsWithName(Project project) {
        return projectRepository.findProjectsByName(project.getName());
    }

    public List<Project> findProjectsWithUser(String username) {
        User queryUser = userService.getUser(username);
        return userService.getProjectByUser(queryUser);
    }

    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    public Project findProjectWithId(Integer project_id){
        return projectRepository.findProjectById(project_id);
    }

    public void deleteProject(Project project){
        projectRepository.delete(project);
    }

}
