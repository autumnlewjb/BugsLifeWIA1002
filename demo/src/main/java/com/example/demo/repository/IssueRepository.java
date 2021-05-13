package com.example.demo.repository;

import java.util.List;

import com.example.demo.models.Issue;
import com.example.demo.models.Project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {
    @Query("select i from Issue i where i.project = ?1")
    List<Issue> findByProject(Project project);

    @Query("select i from Issue i where i.issue_id = ?1")
    Issue findIssueById(Integer issue_id);
}
