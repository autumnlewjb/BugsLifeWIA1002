package com.example.demo.services;

import java.util.List;

import com.example.demo.models.Project;
import com.example.demo.models.User;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public UserService(UserRepository userRepository, ProjectRepository projectRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
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
        userRepository.deleteUserByUsername(username);
    }

    public User getUserById(Integer id) {
        return userRepository.findUserById(id);
    }

    public void createListOfUsers(List<User> users){
        userRepository.saveAll(users);
    }

    public void updateUser(User user, User updatedUser) {
        List<Project> allProject = projectRepository.findProjectsByUser(user);
        for (Project project: allProject ){
            project.setUser(updatedUser);
        }
        updatedUser.setProject(allProject);
        userRepository.save(updatedUser);
    }
}
