package com.example.demo.services;

import com.example.demo.models.Issue;
import com.example.demo.models.Project;
import com.example.demo.models.User;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.engine.search.sort.SearchSort;
import org.hibernate.search.engine.search.sort.dsl.SortOrder;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
                                .fuzzy()
                )
                .fetch((int) pageable.getOffset(), pageable.getPageSize());
        return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
    }

    private Page<Project> searchProjectWithSort(Pageable pageable, String query, List<String> sort) {
        SearchSession session = Search.session(entityManager);
        SearchResult<Project> result = session.search(Project.class)
                .where(
                        f -> f.match()
                                .fields("name", "description")
                                .matching(query).fuzzy()
                )
                .sort(
                        f -> f.field(sort.get(0)).order(SortOrder.valueOf(sort.get(1).toUpperCase()))
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
                                .fuzzy()
                )
                .fetch((int) pageable.getOffset(), pageable.getPageSize());
        return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
    }

    private Page<Issue> searchIssueWithSort(Pageable pageable, String query, List<String> sort) {
        SearchSession session = Search.session(entityManager);
        SearchResult<Issue> result = session.search(Issue.class)
                .where(
                        f -> f.match()
                                .fields("title", "descriptionText", "comment.text")
                                .matching(query).fuzzy()
                )
                .sort(
                        f -> f.field(sort.get(0)).order(SortOrder.valueOf(sort.get(1).toUpperCase()))
                )
                .fetch((int) pageable.getOffset(), pageable.getPageSize());
        return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
    }

    private Page<Issue> searchIssueWithMultipleSort(Pageable pageable, String query, List<String> sort) {
        SearchSession session = Search.session(entityManager);
        SearchResult<Issue> result = session.search(Issue.class)
                .where(
                        f -> f.match()
                                .fields("title", "descriptionText", "comment.text")
                                .matching(query).fuzzy()
                )
                .sort(
                        f -> f.field(sort.get(0)).order(SortOrder.valueOf(sort.get(1).toUpperCase()))
                                .then()
                                .field(sort.get(2)).order(SortOrder.valueOf(sort.get(3).toUpperCase()))
                )
                .fetch((int) pageable.getOffset(), pageable.getPageSize());
        return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
    }

    private Page<Issue> searchIssueWithFilter(Pageable pageable, String query, List<String> filter) {
        SearchSession session = Search.session(entityManager);
        SearchResult<Issue> result = session.search(Issue.class)
                .where(
                        f -> f.bool()
                                .should(f.match().fields("title", "descriptionText", "comment.text")
                                        .matching(query).fuzzy())
                                .must(f.match().field(filter.get(0)).matching(filter.get(1)))
                )
                .fetch((int) pageable.getOffset(), pageable.getPageSize());
        return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
    }

    private Page<Issue> searchIssueWithMultipleFilter(Pageable pageable, String query, List<String> filter) {
        SearchSession session = Search.session(entityManager);
        SearchResult<Issue> result = session.search(Issue.class)
                .where(
                        f -> f.bool()
                                .should(f.match().fields("title", "descriptionText", "comment.text")
                                        .matching(query).fuzzy())
                                .must(f.match().field(filter.get(0)).matching(filter.get(1)))
                                .must(f.match().field(filter.get(2)).matching(filter.get(3)))
                )
                .fetch((int) pageable.getOffset(), pageable.getPageSize());
        return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
    }

    private Page<Issue> searchIssueWithSortAndFilter(Pageable pageable, String query, List<String> sort, List<String> filter) {
        SearchSession session = Search.session(entityManager);
        SearchResult<Issue> result = session.search(Issue.class)
                .where(
                        f -> f.bool()
                                .should(f.match().fields("title", "descriptionText",
                                        "comment.text")
                                        .matching(query).fuzzy())
                                .must(f.match().field(filter.get(0)).matching(filter.get(1)))
                )
                .sort(
                        f -> f.field(sort.get(0)).order(SortOrder.valueOf(sort.get(1).toUpperCase()))
                )
                .fetch((int) pageable.getOffset(), pageable.getPageSize());
        return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
    }

    private Page<Issue> searchIssueWithSortAndMultipleFilter(Pageable pageable, String query, List<String> sort, List<String> filter) {
        SearchSession session = Search.session(entityManager);
        SearchResult<Issue> result = session.search(Issue.class)
                .where(
                        f -> f.bool()
                                .should(f.match().fields("title", "descriptionText",
                                        "comment.text")
                                        .matching(query).fuzzy())
                                .must(f.match().field(filter.get(0)).matching(filter.get(1)))
                                .must(f.match().field(filter.get(2)).matching(filter.get(3)))
                )
                .sort(
                        f -> f.field(sort.get(0)).order(SortOrder.valueOf(sort.get(1).toUpperCase()))
                )
                .fetch((int) pageable.getOffset(), pageable.getPageSize());
        return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
    }

    private Page<Issue> searchIssueWithMultipleSortAndFilter(Pageable pageable, String query, List<String> sort, List<String> filter) {
        SearchSession session = Search.session(entityManager);
        SearchResult<Issue> result = session.search(Issue.class)
                .where(
                        f -> f.bool()
                                .should(f.match().fields("title", "descriptionText",
                                        "comment.text")
                                        .matching(query).fuzzy())
                                .must(f.match().field(filter.get(0)).matching(filter.get(1)))
                                .must(f.match().field(filter.get(2)).matching(filter.get(3)))
                )
                .sort(
                        f -> f.field(sort.get(0)).order(SortOrder.valueOf(sort.get(1).toUpperCase()))
                                .then()
                                .field(sort.get(2)).order(SortOrder.valueOf(sort.get(3).toUpperCase()))
                )
                .fetch((int) pageable.getOffset(), pageable.getPageSize());
        return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
    }

    private Page<Issue> searchIssueWithMultipleSortMultipleFilter(Pageable pageable, String query, List<String> sort, List<String> filter) {
        SearchSession session = Search.session(entityManager);
        SearchResult<Issue> result = session.search(Issue.class)
                .where(
                        f -> f.bool()
                                .should(f.match().fields("title", "descriptionText",
                                        "comment.text")
                                        .matching(query).fuzzy())
                                .must(f.match().field(filter.get(0)).matching(filter.get(1)))
                )
                .sort(
                        f -> f.field(sort.get(0)).order(SortOrder.valueOf(sort.get(1).toUpperCase()))
                                .then()
                                .field(sort.get(2)).order(SortOrder.valueOf(sort.get(3).toUpperCase()))
                )
                .fetch((int) pageable.getOffset(), pageable.getPageSize());
        return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
    }

    public Page<?> search(Pageable pageable, String query, String scope, String[] sortArr, String[] filterArr) {
        List<String> sortList = new ArrayList<>();
        List<String> filterList = new ArrayList<>();
        getList(sortArr, sortList);
        getList(filterArr, filterList);
        boolean needSort = getSort(sortList);
        boolean needFilter = getFilter(filterArr);
        switch (scope) {
            case "user":
                return searchUser(pageable, query);
            case "project":
                if (needSort) {
                    return searchProjectWithSort(pageable, query, sortList);
                }
                return searchProject(pageable, query);
            case "issue":
                if (needSort && needFilter) {
                    if (sortList.size() > 2 && filterList.size() > 2) {
                        return searchIssueWithMultipleSortMultipleFilter(pageable, query, sortList, filterList);
                    } else if (sortList.size() > 2) {
                        return searchIssueWithMultipleSortAndFilter(pageable, query, sortList, filterList);
                    } else if (filterList.size() > 2) {
                        return searchIssueWithSortAndMultipleFilter(pageable, query, sortList, filterList);
                    }
                    return searchIssueWithSortAndFilter(pageable, query, sortList, filterList);

                } else if (needSort) {
                    if (sortList.size() > 2) {
                        return searchIssueWithMultipleSort(pageable, query, sortList);
                    }
                    return searchIssueWithSort(pageable, query, sortList);

                } else if (needFilter) {
                    if (filterList.size() > 2) {
                        return searchIssueWithMultipleFilter(pageable, query, filterList);
                    }
                    return searchIssueWithFilter(pageable, query, filterList);
                }

                return searchIssue(pageable, query);
        }
        return searchAll(pageable, query);
    }

    private void getList(String[] arr, List<String> list) {
        if (arr[0].contains(",")) {
            for (String element : arr) {
                String[] split = element.split(",");
                list.add(split[0]);
                list.add(split[1]);
            }
        } else {
            list.add(arr[0]);
            list.add(arr[1]);
        }
    }

    private boolean getSort(List<String> list) {
        return !list.get(0).equalsIgnoreCase("relevance") || !list.get(1).equalsIgnoreCase("desc");
    }

    private boolean getFilter(String[] filter) {
        return !filter[0].equalsIgnoreCase("none");
    }

}
