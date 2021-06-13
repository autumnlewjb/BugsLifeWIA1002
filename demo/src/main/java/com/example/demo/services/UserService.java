package com.example.demo.services;

import com.example.demo.models.Project;
import com.example.demo.models.User;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Scope("singleton")
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, ProjectRepository projectRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User getUser(String username) {
        return userRepository.findByUsername(username);
    }

    public List<String> getAllUsername() {
        return userRepository.getAllUsername();
    }

    public List<Project> getProjectByUser(User user) {
        return projectRepository.findProjectsByUser(user);
    }

    public void deleteUser(String username) {
        userRepository.deleteUserByUsername(username);
    }

    public User getUserById(Integer id) {
        return userRepository.findUserById(id);
    }

    public void createListOfUsers(List<User> users) {
        userRepository.saveAll(users);
    }

    @PreAuthorize("#updatedUser.username == authentication.name")
    public void updateUser(User user, User updatedUser) {
        updatedUser.setUser_id(user.getUser_id());
        updatedUser.setRoles(user.getRoles());
        if (updatedUser.getPassword().equals(user.getPassword()) ||
                passwordEncoder.matches(updatedUser.getPassword(), user.getPassword())) {
            updatedUser.setPassword(user.getPassword());
        } else {
            updatedUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        List<Project> allProject = projectRepository.findProjectsByUser(user);
        for (Project project : allProject) {
            project.setUser(updatedUser);
        }
        updatedUser.setProject(allProject);
        userRepository.save(updatedUser);
    }
}
