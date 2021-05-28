package com.example.demo.services;

import com.example.demo.models.Issue;
import com.example.demo.models.Project;
import com.example.demo.models.User;
import org.apache.lucene.util.QueryBuilder;
import org.hibernate.Session;
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
import org.springframework.data.jpa.provider.HibernateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
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
        File folder = new File("demo/index");
        if(!folder.exists()) {
            try {
                SearchSession searchSession = Search.session(entityManager);
                MassIndexer indexer = searchSession.massIndexer();
                indexer.startAndWait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Page<?> searchAll(Pageable pageable, String query) {
        SearchSession session = Search.session(entityManager);
        SearchResult<?> result = session.search(Arrays.asList(User.class, Project.class, Issue.class))
                .where(
                        f -> f.match()
                                .fields("username", "email", "name", "description",
                                        "title", "descriptionText", "comment.text")
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

    private Page<Project> searchProject(Pageable pageable, String query, List<String> sort) {
        SearchSession session = Search.session(entityManager);
        SearchResult<Project> result = session.search(Project.class)
                .where(
                        f -> f.match()
                                .fields("name", "description")
                                .matching(query).fuzzy()
                )
                .sort(f -> f.composite(b -> {
                            for (int i = 0; i < sort.size(); i += 2) {
                                b.add(f.field(sort.get(i)).order(SortOrder.valueOf(sort.get(i + 1).toUpperCase())));
                            }
                        })
                )
                .fetch((int) pageable.getOffset(), pageable.getPageSize());
        return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
    }

    private Page<Issue> searchIssue(Pageable pageable, String query, List<String> sort, List<String> filter) {
        SearchSession session = Search.session(entityManager);
        SearchResult<Issue> result = session.search(Issue.class)
                .where(f -> f.bool(b -> {
                            b.must(f.match().fields("title", "descriptionText", "comment.text")
                                    .matching(query).fuzzy());
                            for (int i = 0; i < filter.size(); i += 2) {
                                b.must(f.match().field(filter.get(i)).matching(filter.get(i + 1)));
                            }
                        })
                )
                .sort(f -> f.composite(b -> {
                            for (int i = 0; i < sort.size(); i += 2) {
                                b.add(f.field(sort.get(i)).order(SortOrder.valueOf(sort.get(i + 1).toUpperCase())));
                            }
                        })
                )
                .fetch((int) pageable.getOffset(), pageable.getPageSize());
        return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
    }

    public Page<?> search(Pageable pageable, String query, String scope, String[] sortArr, String[] filterArr) {
        List<String> sortList = new ArrayList<>();
        List<String> filterList = new ArrayList<>();
        getList(sortArr, sortList);
        getList(filterArr, filterList);
        switch (scope) {
            case "user":
                return searchUser(pageable, query);
            case "project":
                return searchProject(pageable, query, sortList);
            case "issue":
                return searchIssue(pageable, query, sortList, filterList);
        }
        return searchAll(pageable, query);
    }

    private void getList(String[] arr, List<String> list) {
        if (arr[0].equals("none")) {
            return;
        }
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

}
