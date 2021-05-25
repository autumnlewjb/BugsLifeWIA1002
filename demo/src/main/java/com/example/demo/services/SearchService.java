package com.example.demo.services;

import com.example.demo.models.Issue;
import com.example.demo.models.Project;
import com.example.demo.models.User;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.engine.search.sort.dsl.SortOrder;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    public Page<?> searchAll(Pageable pageable, String query) {
        SearchSession session = Search.session(entityManager);
        SearchResult<?> result = session.search(Arrays.asList(User.class, Project.class, Issue.class))
                .where(
                        f -> f.match()
                                .fields("username", "email",
                                        "name", "description",
                                        "title", "descriptionText",
                                        "comment.text")
                                .matching(query).fuzzy()
                )
                .fetch((int) pageable.getOffset(), pageable.getPageSize());
        return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
    }

    public Page<User> searchUser(Pageable pageable, String query) {
        SearchSession session = Search.session(entityManager);
        SearchResult<User> result = session.search(User.class)
                .where(
                        f -> f.match().fields("username", "email")
                                .matching(query)
                                .fuzzy()
                )
                .fetch((int) pageable.getOffset(), pageable.getPageSize());
        return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
    }

    public Page<Project> searchProject(Pageable pageable, String query) {
        SearchSession session = Search.session(entityManager);
        SearchResult<Project> result = session.search(Project.class)
                .where(
                        f -> f.match().fields("name", "description")
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

    private Page<Issue> searchWithSortAndFilter(Pageable pageable, String query, String[] sort, String[] filter) {
        SearchSession session = Search.session(entityManager);
        SearchResult<Issue> result = session.search(Issue.class)
                .where(
                        f -> f.bool()
                                .should(f.match().fields("title", "descriptionText",
                                        "comment.text")
                                        .matching(query).fuzzy(1))
                                .must(f.match().field(filter[0]).matching(filter[1]).fuzzy(0))
                )
                .sort(
                        f -> f.field(sort[0]).order(SortOrder.valueOf(sort[1].toUpperCase()))
                )
                .fetch((int) pageable.getOffset(), pageable.getPageSize());
        return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
    }

    private Page<Issue> searchWithSort(Pageable pageable, String query, String[] sort) {
        SearchSession session = Search.session(entityManager);
        SearchResult<Issue> result = session.search(Issue.class)
                .where(
                        f -> f.match()
                                .fields("title", "descriptionText", "comment.text")
                                .matching(query).fuzzy(1)
                )
                .sort(
                        f -> f.field(sort[0]).order(SortOrder.valueOf(sort[1].toUpperCase()))
                )
                .fetch((int) pageable.getOffset(), pageable.getPageSize());
        return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
    }

    private Page<Issue> searchWithFilter(Pageable pageable, String query, String[] filter) {
        SearchSession session = Search.session(entityManager);
        SearchResult<Issue> result = session.search(Issue.class)
                .where(
                        f -> f.bool()
                                .should(f.match().fields("title", "descriptionText", "comment.text")
                                        .matching(query).fuzzy(1))
                                .must(f.match().field(filter[0]).matching(filter[1]).fuzzy(0))
                )
                .fetch((int) pageable.getOffset(), pageable.getPageSize());
        return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
    }

    public Page<?> search(Pageable pageable, Map<String, String> map) {
        String scope = map.get("scope");
        String query = map.get("query");
        String[] sortArr = map.get("sort").split(",");
        String[] filterArr = map.get("filter").split(",");
        boolean needSort = getSort(sortArr);
        boolean needFilter = getFilter(filterArr);
        switch (scope) {
            case "user":
                return searchUser(pageable, query);
            case "project":
                return searchProject(pageable, query);
            case "issue":
                if (needSort && needFilter) {
                    return searchWithSortAndFilter(pageable, query, sortArr, filterArr);
                } else if (needSort) {
                    return searchWithSort(pageable, query, sortArr);
                } else if (needFilter) {
                    return searchWithFilter(pageable, query, filterArr);
                }
                return searchIssue(pageable,query);
        }
        return searchAll(pageable, query);
    }

    private boolean getSort(String[] arr) {
        return !arr[0].equals("relevance") || !arr[1].equals("desc");
    }

    private boolean getFilter(String[] filter) {
        return !filter[0].equals("none");
    }

}
