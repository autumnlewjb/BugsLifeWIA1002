package com.example.demo.repository;

import com.example.demo.models.Issue;
import com.example.demo.models.Project;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {
    @Query("select i from Issue i where i.project = ?1")
    List<Issue> findByProject(Project project);

    @Query("select i from Issue i where i.issue_id = ?1")
    Issue findIssueById(Integer issue_id);

    List<Issue> findByProject(Project project, Sort sort);

    List<Issue> findAllByProjectAndTag(Project project, String tag, Sort sort);

    List<Issue> findAllByProjectAndStatus(Project project, String status, Sort sort);
}
