package com.example.demo.services;

import java.util.List;

import com.example.demo.models.Project;
import com.example.demo.models.User;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserService {
    @Autowired
    private RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public UserService(UserRepository userRepository, ProjectRepository projectRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

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

    public void deleteUser(String username){
        userRepository.delete(getUser(username));
    }

    public User getUserById(Integer id) {
        return userRepository.findUserById(id);
    }

    public void createListOfUsers(List<User> users){
        userRepository.saveAll(users);
    }
}
