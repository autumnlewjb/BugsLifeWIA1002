package com.example.demo.services;

import com.example.demo.models.Issue;
import com.example.demo.models.Project;
import com.example.demo.models.User;
import com.example.demo.repository.IssueRepository;
import com.example.demo.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final IssueRepository issueRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, UserService userService, IssueRepository issueRepository) {
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.issueRepository = issueRepository;
    }

    public List<Project> findAllProjects() {
        return projectRepository.findAll();
    }

    public List<Project> findAllProjectsWithSort(String sort) {
        String[] sortArr = sort.split(",");
        Sort order;
        if (sortArr[0].equalsIgnoreCase("issueNum")) {
            if (sortArr[1].equalsIgnoreCase("asc")) {
                return projectRepository.findAllWithCountAsc();
            } else if (sortArr[1].equalsIgnoreCase("desc")) {
                return projectRepository.findAllWithCountDesc();
            }
        }
        if (sortArr[1].equalsIgnoreCase("asc")) {
            order = Sort.by(Sort.Direction.ASC, sortArr[0]);
        } else {
            order = Sort.by(Sort.Direction.DESC, sortArr[0]);
        }
        return projectRepository.findAll(order);
    }

    public List<Project> findProjectsWithName(Project project) {
        return projectRepository.findProjectsByName(project.getName());
    }

    public List<Project> findProjectsWithUser(String username) {
        User queryUser = userService.getUser(username);
        return userService.getProjectByUser(queryUser);
    }

    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    public Project findProjectWithId(Integer project_id) {
        return projectRepository.findProjectById(project_id);
    }

    @PreAuthorize("#oldProject.user.username == authentication.name")
    public void updateProject(Project oldProject, Project updatedProject) {
        updatedProject.setProject_id(oldProject.getProject_id());
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
