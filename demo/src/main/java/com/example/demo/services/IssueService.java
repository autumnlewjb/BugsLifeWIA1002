package com.example.demo.services;

import java.util.List;

import com.example.demo.models.Comment;
import com.example.demo.models.Issue;
import com.example.demo.models.Project;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.IssueRepository;
import javax.transaction.Transactional;

import com.example.demo.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class IssueService {

    private final IssueRepository issueRepository;
    private final ProjectRepository projectRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public IssueService(IssueRepository issueRepository, ProjectRepository projectRepository, CommentRepository commentRepository) {
        this.issueRepository = issueRepository;
        this.projectRepository = projectRepository;
        this.commentRepository = commentRepository;
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

    @PreAuthorize("#issue.createdBy == authentication.name")
    public void updateIssue(Integer project_id, Issue issue, Issue updatedIssue) {
        updatedIssue.setIssue_id(issue.getIssue_id());
        List<Comment> allComments = commentRepository.findByIssue(issue);
        Project project = projectRepository.findProjectById(project_id);
        project.getIssue().set(project.getIssueIndex(issue), updatedIssue);
        for (Comment comment : allComments){
            comment.setIssue(updatedIssue);
        }
        updatedIssue.setProject(project);
        updatedIssue.setComment(allComments);
        issueRepository.save(updatedIssue);
    }

    @PreAuthorize("#issue.createdBy == authentication.name")
    public void deleteIssue(Project project, Issue issue){
        project.getIssue().remove(issue);
        issue.setProject(null);
        issueRepository.delete(issue);
    }

    public List<Issue> findAll() {
        return issueRepository.findAll();
    }

}
