package com.example.demo.services;

import java.util.List;

import com.example.demo.models.Issue;
import com.example.demo.models.Project;
import com.example.demo.repository.IssueRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IssueService {

    private final IssueRepository issueRepository;

    @Autowired
    public IssueService(IssueRepository issueRepository) {
        this.issueRepository = issueRepository;
    }

    public List<Issue> findIssuesByProject(Project project) {
        return issueRepository.findByProject(project);
    }

    public Issue createIssue(Issue issue) {
        return issueRepository.save(issue);
    }


    public Issue findIssuesById(Integer issue_id) {
        return issueRepository.findIssueById(issue_id);
    }
    
    public void deleteIssue(Issue issue){
        issueRepository.delete(issue);
    }
}
