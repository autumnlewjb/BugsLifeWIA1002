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
import java.util.Arrays;
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

    private Page<Project> searchProjectWithSort(Pageable pageable, String query, String[] sort) {
        SearchSession session = Search.session(entityManager);
        SearchResult<Project> result = session.search(Project.class)
                .where(
                        f -> f.match()
                                .fields("name", "description")
                                .matching(query).fuzzy(1)
                )
                .sort(
                        f -> f.field(sort[0]).order(SortOrder.valueOf(sort[1].toUpperCase()))
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

    private Page<Issue> searchIssueWithSortAndFilter(Pageable pageable, String query, String[] sort, String[] filter) {
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

    private Page<Issue> searchIssueWithSort(Pageable pageable, String query, String[] sort) {
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

    private Page<Issue> searchIssueWithFilter(Pageable pageable, String query, String[] filter) {
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

    public Page<Issue> searchComment(Pageable pageable, String query) {
        SearchSession session = Search.session(entityManager);
        SearchResult<Issue> result = session.search(Issue.class)
                .where(
                        f -> f.match().fields("comment.text")
                                .matching(query)
                                .fuzzy(1)
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
                if (needSort) {
                    return searchProjectWithSort(pageable, query, sortArr);
                }
                return searchProject(pageable, query);
            case "issue":
                if (needSort && needFilter) {
                    return searchIssueWithSortAndFilter(pageable, query, sortArr, filterArr);
                } else if (needSort) {
                    return searchIssueWithSort(pageable, query, sortArr);
                } else if (needFilter) {
                    return searchIssueWithFilter(pageable, query, filterArr);
                }
                return searchIssue(pageable, query);
            case "comment":
                return searchComment(pageable, query);
        }
        return searchAll(pageable, query);
    }

    private boolean getSort(String[] arr) {
        return !arr[0].equalsIgnoreCase("relevance") || !arr[1].equalsIgnoreCase("desc");
    }

    private boolean getFilter(String[] filter) {
        return !filter[0].equalsIgnoreCase("none");
    }

}
