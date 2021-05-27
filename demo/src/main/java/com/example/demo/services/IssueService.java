package com.example.demo.services;

import com.example.demo.models.Comment;
import com.example.demo.models.Issue;
import com.example.demo.models.Project;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.IssueRepository;
import com.example.demo.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

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

    public List<Issue> findIssuesWithSortAndFilter(String[] sortArr, String[] filterArr, Project project) {
        List<Sort.Order> orders = new ArrayList<>();
        if (sortArr[0].contains(",")) {
            for (String element : sortArr) {
                String[] sort = element.split(",");
                orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
            }
        } else {
            orders.add(new Sort.Order(getSortDirection(sortArr[1]), sortArr[0]));
        }
        if (filterArr[0].contains(",")) {
            String[] statusFilter;
            String[] tagFilter;
            if (filterArr[0].contains("status")) {
                statusFilter = filterArr[0].split(",");
                tagFilter = filterArr[1].split(",");
            } else {
                statusFilter = filterArr[1].split(",");
                tagFilter = filterArr[0].split(",");
            }
            return issueRepository.findAllByProjectAndStatusAndTag(project, statusFilter[1], tagFilter[1], Sort.by(orders));
        } else {
            if (filterArr[0].equalsIgnoreCase("status")) {
                return issueRepository.findAllByProjectAndStatus(project, filterArr[1], Sort.by(orders));
            } else if (filterArr[0].equalsIgnoreCase("tag")) {
                return issueRepository.findAllByProjectAndTag(project, filterArr[1], Sort.by(orders));
            }
        }
        return issueRepository.findByProject(project, Sort.by(orders));
    }

    private Sort.Direction getSortDirection(String direction) {
        if (direction.equalsIgnoreCase("asc")) {
            return Sort.Direction.ASC;
        }
        return Sort.Direction.DESC;
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
        for (Comment comment : allComments) {
            comment.setIssue(updatedIssue);
        }
        updatedIssue.setProject(project);
        updatedIssue.setComment(allComments);
        issueRepository.save(updatedIssue);
    }

    @PreAuthorize("#issue.createdBy == authentication.name")
    public void deleteIssue(Project project, Issue issue) {
        project.getIssue().remove(issue);
        issue.setProject(null);
        issueRepository.delete(issue);
    }

    public List<Issue> findAll() {
        return issueRepository.findAll();
    }

}
