package com.example.demo.controllers;

import com.example.demo.models.Issue;
import com.example.demo.models.Project;
import com.example.demo.services.IssueService;
import com.example.demo.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @GetMapping("/{project_id}/issues")
    public ResponseEntity<List<Issue>> getIssueByProject(@PathVariable Integer project_id) {
        Project project = projectService.findProjectWithId(project_id);
        List<Issue> issueList = issueService.findIssuesByProject(project);
        return new ResponseEntity<>(issueList, HttpStatus.OK);
    }

    // FIXME this endpoints create new user in the author field although the user exist
    @PostMapping("/{project_id}/issue/create")
    public ResponseEntity<Issue> createIssue(@RequestBody Issue issue, @PathVariable Integer project_id) {
        Project project = projectService.findProjectWithId(project_id);
        project.getIssue().add(issue);
        issue.setProject(project);
        issueService.createIssue(issue);
        return new ResponseEntity<>(issue, HttpStatus.OK);
    }

    @DeleteMapping("/project/{project_id}/delete/issue/{issue_id}")
    public ResponseEntity<Issue> deleteIssue(@PathVariable Integer project_id, @PathVariable Integer issue_id) {
        Project project = projectService.findProjectWithId(project_id);
        Issue issue = issueService.findIssuesById(issue_id);
        project.getIssue().remove(issue);
        issue.setProject(null);
        issueService.deleteIssue(issue);
        return new ResponseEntity<>(issue, HttpStatus.OK);
    }

    @PutMapping("{project_id}/{issue_id}/updateIssue")
    public ResponseEntity<?> updateIssue(@PathVariable Integer project_id, @PathVariable Integer issue_id, @RequestBody Issue updatedIssue){
        Issue issue = issueService.findIssuesById(issue_id);
        updatedIssue.setIssue_id(issue.getIssue_id());
        issueService.updateIssue(project_id, issue, updatedIssue);
        return new ResponseEntity<>(updatedIssue, HttpStatus.OK);
    }
}
