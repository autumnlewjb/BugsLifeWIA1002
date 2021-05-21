package com.example.demo.services;

import com.example.demo.models.Issue;
import com.example.demo.models.Project;
import com.example.demo.models.User;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class SearchService implements ApplicationListener<ApplicationReadyEvent> {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        try {
            SearchSession searchSession = Search.session(entityManager);
            MassIndexer indexer = searchSession.massIndexer();
            indexer.startAndWait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Page<User> search(Pageable pageable, String query) {
        SearchSession session = Search.session(entityManager);
        SearchResult<User> result = session.search(User.class)
                .where(
                        f -> f.match().fields("username", "email", "project.name", "project.description")
                                .matching(query)
                                .fuzzy(1)
                )
                .fetch((int) pageable.getOffset(), pageable.getPageSize());
        return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
    }

    public Page<Project> searchProject(Pageable pageable, String query) {
        SearchSession session = Search.session(entityManager);
        SearchResult<Project> result = session.search(Project.class)
                .where(
                        f -> f.match().fields("name", "description", "issue.title", "issue.descriptionText")
                                .matching(query)
                                .fuzzy(1)
                )
                .fetch((int) pageable.getOffset(), pageable.getPageSize());
        return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
    }

    public Page<Issue> searchIssue(Pageable pageable, String query) {
        SearchSession session = Search.session(entityManager);
        SearchResult<Issue> result = session.search(Issue.class)
                .where(
                        f -> f.match().fields("title", "descriptionText", "comment.text")
                                .matching(query)
                                .fuzzy(1)
                )
                .fetch((int) pageable.getOffset(), pageable.getPageSize());
        return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
    }

    public Page<?> searchMultiple(Pageable pageable, String query, String scope) {
        List<String> list = new ArrayList<>();
        List<Class<?>> classList =new ArrayList<>();
        if (scope.contains("all")) {
            scope = "user+project+issue+comment";
        }
        if (scope.contains("user")) {
            list.add("username");
            list.add("email");
            classList.add(User.class);
        }
        if (scope.contains("project")) {
            list.add("name");
            list.add("description");
            classList.add(Project.class);
        }
        if (scope.contains("issue")) {
            list.add("issue.title");
            list.add("issue.descriptionText");
            classList.add(Project.class);
        }
        if (scope.contains("comment")) {
            list.add("issue.comment.text");
            classList.add(Project.class);
        }
        return search(pageable, query, list.toArray(new String[0]), classList);
    }

    public Page<?> search(Pageable pageable, String query, String[] fields, List<Class<?>> classList) {
        SearchSession session = Search.session(entityManager);
        SearchResult<?> result = session.search(classList)
                .where(
                        f -> f.match()
                                .fields(fields)
                                .matching(query).fuzzy(1)
                )
                .fetch((int) pageable.getOffset(), pageable.getPageSize());
        return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
    }

}
