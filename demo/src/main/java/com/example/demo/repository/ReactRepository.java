package com.example.demo.repository;

import com.example.demo.models.Comment;
import com.example.demo.models.React;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReactRepository extends JpaRepository<React, Long> {

    @Query("select r from React r where r.comment = ?1")
    List<React> findReactionByComment(Comment comment);

    @Query("select r from React r where r.react_id = ?1")
    React findReactionByID(Integer react_id);

}
