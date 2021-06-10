package com.example.demo.services;

import com.example.demo.models.Issue;
import com.example.demo.models.Project;
import com.example.demo.models.User;
import com.example.demo.repository.IssueRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final IssueRepository issueRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, UserService userService, UserRepository userRepository, IssueRepository issueRepository) {
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.issueRepository = issueRepository;
    }

    public List<Project> findAllProjects() {
        return projectRepository.findAll();
    }

    public List<Project> findAllProjectsWithSort(String[] sortArr) {
        List<Order> orders = new ArrayList<>();
        if (sortArr[0].contains(",")) {
            for (String element : sortArr) {
                String[] sort = element.split(",");
                orders.add(new Order(getSortDirection(sort[1]), sort[0]));
            }
        } else {
            orders.add(new Order(getSortDirection(sortArr[1]), sortArr[0]));
        }
        return projectRepository.findAll(Sort.by(orders));
    }

    private Sort.Direction getSortDirection(String direction) {
        if (direction.equalsIgnoreCase("asc")) {
            return Sort.Direction.ASC;
        }
        return Sort.Direction.DESC;
    }

    public List<Project> findProjectsWithName(Project project) {
        return projectRepository.findProjectsByName(project.getName());
    }

    public List<Project> findProjectsWithName(String projectName) {
        return projectRepository.findProjectsByName(projectName);
    }

    public List<Project> findProjectsWithUser(String username) {
        User queryUser = userService.getUser(username);
        return userService.getProjectByUser(queryUser);
    }

    public Project createProject(Integer user_id, Project project) {
        User user = userRepository.findUserById(user_id);
        project.setUser(user);
        user.getProject().add(project);
        return projectRepository.save(project);
    }

    public Project findProjectWithId(Integer project_id) {
        return projectRepository.findProjectById(project_id);
    }

    @PreAuthorize("#oldProject.user.username == authentication.name")
    public void updateProject(Project oldProject, Project updatedProject) {
        updatedProject.setProjectId(oldProject.getProjectId());
        //find all issues from old project
        List<Issue> allIssue = issueRepository.findByProject(oldProject);
        //find user related to old project
        User user = oldProject.getUser();
        //set updatedProject to user
        user.getProject().set(user.getProjectIndex(oldProject), updatedProject);
        //set updatedProject to all issues
        for (Issue issue : allIssue) {
            issue.setProject(updatedProject);
        }
        //set user for the updatedProject
        updatedProject.setUser(user);
        //set issues for the updatedProject
        updatedProject.setIssue(allIssue);
        projectRepository.save(updatedProject);
    }

    @PreAuthorize("#project.user.username == authentication.name")
    public void deleteProject(Project project) {
        project.getUser().getProject().remove(project);
        project.removeUser();
        projectRepository.delete(project);
    }
}
