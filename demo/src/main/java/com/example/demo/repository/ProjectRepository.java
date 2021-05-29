package com.example.demo.repository;

import com.example.demo.models.Project;
import com.example.demo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("select p from Project p where p.name = ?1")
    List<Project> findProjectsByName(String name);

    @Query("select p from Project p where ?1 in p.user")
    List<Project> findProjectsByUser(User user);

    @Query("select p from Project p where p.project_id = ?1")
    Project findProjectById(Integer id);

}
