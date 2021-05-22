package com.example.demo.controllers;

import java.util.List;
import java.util.Optional;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.models.Project;
import com.example.demo.models.User;
import com.example.demo.services.ProjectService;
import com.example.demo.services.UserService;

import javax.transaction.Transactional;

import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;

    @Autowired
    public ProjectController(ProjectService projectService, UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
    }

    @GetMapping("/projects")
    public ResponseEntity<List<Project>> getAllProjects() {
        List<Project> projectList = projectService.findAllProjects();
        return new ResponseEntity<>(projectList, HttpStatus.OK);
    }

    @GetMapping("/project")
    public ResponseEntity<List<Project>> getProjectsWithName(@RequestBody Project project) {
        List<Project> projectList = projectService.findProjectsWithName(project);
        return new ResponseEntity<>(projectList, HttpStatus.OK);
    }

    @GetMapping("/{username}/projects")
    public ResponseEntity<List<Project>> getProjectsWithUser(@PathVariable String username) {
        List<Project> projectList = projectService.findProjectsWithUser(username);
        return new ResponseEntity<>(projectList, HttpStatus.OK);
    }

    @PostMapping("/project/create")
    public ResponseEntity<Project> createProject(@RequestBody Project project) {
        projectService.createProject(project);
        return new ResponseEntity<>(project, HttpStatus.OK);
    }

    @PostMapping("{userId}/project/create")
    public ResponseEntity<Project> createProjectWithUserId(@PathVariable Integer userId, @RequestBody Project project) {
        // find user based on userId
        User user = userService.getUserById(userId);
        // relate user to project (set project.user = user object)
        project.setUser(user);
        user.getProject().add(project);
        // save project
        projectService.createProject(project);
        return new ResponseEntity<>(project, HttpStatus.OK);
    }

    @DeleteMapping("{username}/{project_id}")
    public ResponseEntity<?> deleteProjectByAdmin(@PathVariable String username, @PathVariable Integer project_id) {
        User user = userService.getUser(username);
        Authentication authentication= org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if(projectService.findProjectWithId(project_id).getUser().getUsername().equals(authentication.getName())) {
            Project project = projectService.findProjectWithId(project_id);
            project.getUser().getProject().remove(project);
            project.removeUser();
            projectService.deleteProject(project); 
            return new ResponseEntity<>(project, HttpStatus.OK);
        }
        return null;
        /*Project project = projectService.findProjectWithId(project_id);
        if (project == null) {
            throw new ResourceNotFoundException("project", "id", project_id);
        }
        projectService.deleteProject(project);
        return new ResponseEntity<>(HttpStatus.OK);*/
    }

    @PutMapping("{username}/{project_id}/update")
    public ResponseEntity<?> updateProject(@PathVariable String username, @PathVariable Integer project_id, @RequestBody Project updatedProject) {
        Project project = projectService.findProjectWithId(project_id);
        updatedProject.setProject_id(project.getProject_id());
        projectService.updateProject(username, project, updatedProject);
        return new ResponseEntity<>(updatedProject, HttpStatus.OK);
    }

}
