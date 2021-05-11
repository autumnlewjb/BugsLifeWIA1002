package com.example.demo.repository;

import java.util.List;

import com.example.demo.models.Comment;
import com.example.demo.models.Issue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByIssue(Issue issue);
    
    @Query("select c from Comment c where c.comment_id = ?1")
    Comment findCommentById(Integer comment_id);
}
