package com.example.demo.controllers;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.models.Issue;
import com.example.demo.models.Project;
import com.example.demo.models.User;
import com.example.demo.services.IssueService;
import com.example.demo.services.ProjectService;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api")
public class IssueController {

    private final IssueService issueService;
    private final ProjectService projectService;

    @Autowired
    public IssueController(IssueService issueService, ProjectService projectService) {
        this.issueService = issueService;
        this.projectService = projectService;
    }

    // FIXME this view is giving TransientObjectException probably due to the cascade type
    @GetMapping("/{project_id}")
    public ResponseEntity<List<Issue>> getAllIssues(@PathVariable Integer project_id) {
        Project project = projectService.findProjectWithId(project_id);
        if (project == null) {
            throw new ResourceNotFoundException("project", "id", project_id);
        }
        List<Issue> issueList = issueService.findIssuesByProject(project);
        return ResponseEntity.ok(issueList);
    }

    // FIXME this endpoints create new user in the author field although the user exist
    @PostMapping("/{project_id}")
    public ResponseEntity<Issue> createIssue(@PathVariable Integer project_id, @RequestBody Issue issue) {
        Project project = projectService.findProjectWithId(project_id);
        if (project == null) {
            throw new ResourceNotFoundException("project", "id", project_id);
        }
        project.getIssue().add(issue);
        issue.setProject(project);
        issueService.createIssue(issue);
        return ResponseEntity.ok(issue);
    }

    @PutMapping("{project_id}/{issue_id}")
    public ResponseEntity<?> updateIssue(@PathVariable Integer project_id, @PathVariable Integer issue_id, @RequestBody Issue updatedIssue){
        Issue issue = issueService.findIssuesById(issue_id);
        if (issue == null) {
            throw new ResourceNotFoundException("issue", "id", issue_id);
        }
        issueService.updateIssue(project_id, issue, updatedIssue);
        return ResponseEntity.ok(HttpStatus.OK);
    }
    
    @Transactional
    @DeleteMapping("/{project_id}/{issue_id}")
    public ResponseEntity<?> deleteIssue(@PathVariable Integer project_id, @PathVariable Integer issue_id) {
        Project project = projectService.findProjectWithId(project_id);
        if (project == null) {
            throw new ResourceNotFoundException("project", "id", project_id);
        }
        Issue issue = issueService.findIssuesById(issue_id);
        if (issue == null) {
            throw new ResourceNotFoundException("issue", "id", issue_id);
        }
        issueService.deleteIssue(project, issue);
        return ResponseEntity.ok(HttpStatus.OK);
        /*User user=userService.getUserById(project.getUser().getUser_id());
        Authentication authentication= org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if(projectService.findProjectWithId(project_id).getUser().getUsername().equals(authentication.getName())) {
            Issue issue = issueService.findIssuesById(issue_id);
            project.getIssue().remove(issue);
            issue.setProject(null);
            issueService.deleteIssue(issue);
            return new ResponseEntity<>(issue, HttpStatus.OK);
        }
        return null;*/
    }

}
