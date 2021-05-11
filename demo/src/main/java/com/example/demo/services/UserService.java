package com.example.demo.services;

import java.util.List;

import com.example.demo.models.Project;
import com.example.demo.models.User;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    ProjectRepository projectRepository;

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User getUser(String username) {
        return userRepository.findByUsername(username);
    }

    public List<Project> getProjectByUser(User user) {
        return projectRepository.findProjectsByUser(user); 
    }
}
