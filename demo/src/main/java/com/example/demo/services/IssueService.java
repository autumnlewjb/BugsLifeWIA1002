package com.example.demo.services;

import java.util.List;

import com.example.demo.models.Issue;
import com.example.demo.models.Project;
import com.example.demo.repository.IssueRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IssueService {
    
    @Autowired
    IssueRepository issueRepository;

    public List<Issue> findIssuesByProject(Project project) {
        return issueRepository.findByProject(project);
    }

    public Issue createIssue(Issue issue) {
        return issueRepository.save(issue);
    }
}
