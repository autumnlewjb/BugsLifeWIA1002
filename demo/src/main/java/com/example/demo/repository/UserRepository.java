package com.example.demo.repository;

import com.example.demo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u where u.username = ?1")
    User findByUsername(String username);

    @Query("select u from User u where u.id = ?1")
    User findUserById(Integer id);

    List<User> deleteUserByUsername(String username);

    @Query("select u.username from User u")
    List<String> getAllUsername();
}
