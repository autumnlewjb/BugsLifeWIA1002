package com.example.demo.repository;

import java.util.List;

import com.example.demo.models.Project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("select p from Project p where p.name = ?1")
    List<Project> findProjectsByName(String name);

    @Query("select p from Project p where p.user.id = ?1")
    List<Project> findProjectsByUser(Integer userId);
}
