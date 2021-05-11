package com.example.demo.controllers;

import java.util.List;

import com.example.demo.models.Issue;
import com.example.demo.models.Project;
import com.example.demo.services.IssueService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class IssueController {
    
    @Autowired
    IssueService issueService;

    // FIXME this view is giving TransientObjectException probably due to the cascade type
    @GetMapping("/issues/project")
    public List<Issue> getIssueByProject(@RequestBody Project project) {
        return issueService.findIssuesByProject(project);
    }

    // FIXME this endpoints create new user in the author field although the user exist
    @PostMapping("/issue/create")
    public Issue createIssue(@RequestBody Issue issue) {
        return issueService.createIssue(issue);
    }
}
