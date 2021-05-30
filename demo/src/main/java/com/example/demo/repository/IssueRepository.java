package com.example.demo.repository;

import com.example.demo.models.Issue;
import com.example.demo.models.Project;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long>, JpaSpecificationExecutor<Issue> {
    @Query("select i from Issue i where i.project = ?1")
    List<Issue> findByProject(Project project);

    @Query("select i from Issue i where i.issueId = ?1")
    Issue findIssueById(Integer issue_id);

    List<Issue> findByProject(Project project, Sort sort);

}
