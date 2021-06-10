package com.example.demo.services;

import com.example.demo.models.Comment;
import com.example.demo.models.Issue;
import com.example.demo.models.Project;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.IssueRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.specification.IssueSpecification;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class IssueService {

    @Autowired
    EntityManager entityManager;
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

    public List<Issue> findIssuesByProjectWithSortAndFilter(String[] sortArr, String[] filterArr, Project project) {
        List<Sort.Order> orders = new ArrayList<>();
        List<String> filterList = new ArrayList<>();
        if (sortArr[0].contains(",")) {
            for (String element : sortArr) {
                String[] sort = element.split(",");
                orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
            }
        } else {
            orders.add(new Sort.Order(getSortDirection(sortArr[1]), sortArr[0]));
        }
        if (filterArr[0].contains(",")) {
            for (String element : filterArr) {
                String[] split = element.split(",");
                filterList.add(split[0]);
                filterList.add(split[1]);
            }
        } else {
            if (!filterArr[0].equals("none")) {
                filterList.add(filterArr[0]);
                filterList.add(filterArr[1]);
            }
        }
        if (!filterList.isEmpty()) {
            return issueRepository
                    .findAll(IssueSpecification.belongsToProject(project)
                            .and(IssueSpecification.getSpecificationFromFilters(filterList)));
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

    @PreAuthorize("#issue.createdBy == authentication.name or issue.assignee == authentication.name")
    public void updateIssue(Integer project_id, Issue issue, Issue updatedIssue) {
        updatedIssue.setIssueId(issue.getIssueId());
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

    @PreAuthorize("#issue.createdBy == authentication.name or issue.assignee == authentication.name")
    public void deleteIssue(Project project, Issue issue) {
        project.getIssue().remove(issue);
        issue.setProject(null);
        issueRepository.delete(issue);
    }

    public List<Issue> findAll() {
        return issueRepository.findAll();
    }

    public List<?> getHistory(Integer issue_id) {
        List<?> issueHistory = AuditReaderFactory.get(entityManager).createQuery().forRevisionsOfEntity(Issue.class, true, true).add(AuditEntity.id().eq(issue_id)).addOrder(AuditEntity.revisionNumber().desc()).getResultList();
        return issueHistory;
    }

    public List<?> getOwnHistory(String username) {
        List<?> issueHistory = AuditReaderFactory.get(entityManager).createQuery().forRevisionsOfEntity(Issue.class, true, true).add(AuditEntity.property("modifiedBy").eq(username)).addOrder(AuditEntity.revisionNumber().desc()).getResultList();
        return issueHistory;
    }

    public List<?> getAllHistory() {
        List<?> issueHistory = AuditReaderFactory.get(entityManager).createQuery().forRevisionsOfEntity(Issue.class, true, true).addOrder(AuditEntity.revisionNumber().desc()).getResultList();
        return issueHistory;
    }

    public HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm) {
        List<Map.Entry<String, Integer>> list = new LinkedList<>(hm.entrySet());

        list.sort(Map.Entry.<String, Integer>comparingByValue().reversed());

        HashMap<String, Integer> temp = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    public List<Issue> findIssuesByCreatedBy(String createdBy) {
        List<Issue> allIssues = issueRepository.findAll();
        List<Issue> issuesCreatedBy = new ArrayList<>();
        for (Issue issue : allIssues) {
            if (issue.getCreatedBy().equals(createdBy)) {
                issuesCreatedBy.add(issue);
            }
        }
        return issuesCreatedBy;
    }

    public List<Issue> findIssuesByStatus(Integer project_id, String status) {
        List<Issue> allIssues = issueRepository.findByProject(projectRepository.findProjectById(project_id));
        List<Issue> issueList = new ArrayList<>();
        for (Issue issue : allIssues) {
            if (issue.getStatus().equalsIgnoreCase(status)) {
                issueList.add(issue);
            }
        }
        return issueList;
    }
}
