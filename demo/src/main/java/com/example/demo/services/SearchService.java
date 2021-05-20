package com.example.demo.services;

import com.example.demo.models.Comment;
import com.example.demo.models.Issue;
import com.example.demo.models.Project;
import com.example.demo.models.User;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;

@Service
public class SearchService {

    @PersistenceContext
    EntityManager entityManager;

    @Transactional(readOnly = true)
    public Page<User> search(Pageable pageable, String query) {
        SearchSession session = Search.session(entityManager);
        SearchResult<User> result = session.search(User.class)
                .where(
                        f -> f.match().fields("username","email", "project.name", "project.description")
                                .matching(query)
                                .fuzzy(1)
                )
                .fetch((int) pageable.getOffset(), pageable.getPageSize());
        return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
    }

    @Transactional(readOnly = true)
    public Page<Project> searchProject(Pageable pageable, String query) {
        SearchSession session = Search.session(entityManager);
        SearchResult<Project> result = session.search(Project.class)
                .where(
                        f -> f.match().fields("name","description", "issue.title", "issue.descriptionText")
                                .matching(query)
                                .fuzzy(1)
                )
                .fetch((int) pageable.getOffset(), pageable.getPageSize());
        return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
    }

    @Transactional(readOnly = true)
    public Page<Issue> searchIssue(Pageable pageable, String query) {
        SearchSession session = Search.session(entityManager);
        SearchResult<Issue> result = session.search(Issue.class)
                .where(
                        f -> f.match().fields("title","descriptionText", "comment.text")
                                .matching(query)
                                .fuzzy(1)
                )
                .fetch((int) pageable.getOffset(), pageable.getPageSize());
        return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
    }

}
