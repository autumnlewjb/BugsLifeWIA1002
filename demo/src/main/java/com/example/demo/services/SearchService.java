package com.example.demo.services;

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

@Service
public class SearchService {

    @PersistenceContext
    EntityManager entityManager;

    @Transactional(readOnly = true)
    public Page<User> search(Pageable pageable, String query) {
        SearchSession session = Search.session(entityManager);
        SearchResult<User> result = session.search(User.class)
                .where(
                        f -> f.match().fields("username")
                                .matching(query)
                                .fuzzy(1)
                )
                .fetch((int) pageable.getOffset(), pageable.getPageSize());
        return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
    }
}
