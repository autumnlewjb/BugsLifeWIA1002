package com.example.demo.controllers;

import java.util.List;

import com.example.demo.models.Issue;
import com.example.demo.models.Project;
import com.example.demo.services.IssueService;

import com.example.demo.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class IssueController {
    
    @Autowired
    IssueService issueService;

    @Autowired
    ProjectService projectService;

    // FIXME this view is giving TransientObjectException probably due to the cascade type
    @GetMapping("/{project_id}/issues")
    public List<Issue> getIssueByProject(@PathVariable Integer project_id) {
        Project project = projectService.findProjectWithId(project_id);
        return issueService.findIssuesByProject(project);
    }

    // FIXME this endpoints create new user in the author field although the user exist
    @PostMapping("/{project_id}/issue/create")
    public Issue createIssue(@RequestBody Issue issue, @PathVariable Integer project_id) {
        Project project = projectService.findProjectWithId(project_id);
        project.getIssue().add(issue);
        issue.setProject(project);
        return issueService.createIssue(issue);
    }
    
    @DeleteMapping("/project/{project_id}/delete/issue/{issue_id}")
    public void deleteIssue(@PathVariable Integer project_id, @PathVariable Integer issue_id){
        Project project=projectService.findProjectWithId(project_id);
        Issue issue=issueService.findIssuesById(issue_id);
        project.getIssue().remove(issue);
        issue.setProject(null);
        issueService.deleteIssue(issue);
    }
}
