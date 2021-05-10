package com.example.demo.repository;

import java.util.List;

import com.example.demo.models.Comment;
import com.example.demo.models.Issue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByIssue(Issue issue);
}
