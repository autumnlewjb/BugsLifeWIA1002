package com.example.demo.repository;

import java.util.List;

import com.example.demo.models.Project;
import com.example.demo.models.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("select p from Project p where p.name = ?1")
    List<Project> findProjectsByName(String name);

    @Query("select p from Project p where ?1 in p.user")
    List<Project> findProjectsByUser(User user);

    @Query("select p from Project p where p.project_id = ?1")
    Project findProjectById(Integer id);

    @Query(
            value = "select p from Project p join p.issue ad group by p Order By p.issue.size asc ",
            countQuery = "select count(p) from Project p"
    )
    List<Project> findAllWithCountAsc();

    @Query(
            value = "select p from Project p join p.issue ad group by p Order By p.issue.size desc ",
            countQuery = "select count(p) from Project p"
    )
    List<Project> findAllWithCountDesc();
}
