package com.example.demo.controllers;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.models.Project;
import com.example.demo.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/")
    public ResponseEntity<List<Project>> getAllProjects() {
        List<Project> projectList = projectService.findAllProjects();
        return ResponseEntity.ok(projectList);
    }

    @PostMapping("/")
    public ResponseEntity<Project> createProject(@RequestBody Project project) {
        projectService.createProject(project);
        return ResponseEntity.ok(project);
    }

    @PutMapping("/{project_id}")
    public ResponseEntity<?> updateProject(@PathVariable Integer project_id, @RequestBody Project updatedProject) {
        Project project = projectService.findProjectWithId(project_id);
        if (project == null) {
            throw new ResourceNotFoundException("project", "id", project_id);
        }
        projectService.updateProject(project, updatedProject);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{project_id}")
    public ResponseEntity<?> deleteProject(@PathVariable Integer project_id) {
       /* User user = userService.getUser(username);
        Authentication authentication= org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if(projectService.findProjectWithId(project_id).getUser().getUsername().equals(authentication.getName())) {
            Project project = projectService.findProjectWithId(project_id);
            project.getUser().getProject().remove(project);
            project.removeUser();
            projectService.deleteProject(project); 
            return new ResponseEntity<>(project, HttpStatus.OK);
        }
        return null;*/
        Project project = projectService.findProjectWithId(project_id);
        if (project == null) {
            throw new ResourceNotFoundException("project", "id", project_id);
        }
        projectService.deleteProject(project);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    /*@PreAuthorize("#username == authentication.name")
    @GetMapping("/user/{username}/projects")
    public ResponseEntity<List<Project>> getProjectsByUser(@PathVariable String username) {
        List<Project> projectList = projectService.findProjectsWithUser(username);
        return ResponseEntity.ok(projectList);
    }

    @GetMapping("/project")
    public ResponseEntity<List<Project>> getProjectsWithName(@RequestBody Project project) {
        List<Project> projectList = projectService.findProjectsWithName(project);
        return new ResponseEntity<>(projectList, HttpStatus.OK);
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
    }*/

}
