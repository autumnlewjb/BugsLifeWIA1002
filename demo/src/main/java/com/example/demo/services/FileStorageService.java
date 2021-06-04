package com.example.demo.services;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.models.Attachment;
import com.example.demo.models.Issue;
import com.example.demo.models.Project;
import com.example.demo.repository.FileStorageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class FileStorageService {

    private final FileStorageRepository fileStorageRepository;
    private final ProjectService projectService;
    private final IssueService issueService;

    @Autowired
    public FileStorageService(FileStorageRepository fileStorageRepository, ProjectService projectService, IssueService issueService) {
        this.fileStorageRepository = fileStorageRepository;
        this.projectService = projectService;
        this.issueService = issueService;
    }

    public Attachment storeFile(MultipartFile file, String parent, String id) throws IOException {
        String fileName = file.getOriginalFilename();
        String path = "/var/www/html/";
        FileOutputStream out = new FileOutputStream(path + fileName);
        out.write(file.getBytes());
        out.flush();
        out.close();

        Attachment newAttachment = new Attachment(fileName, file.getContentType(), "/image/" + fileName);
        if (parent.equals("project")) {
            Project project = projectService.findProjectWithId(Integer.parseInt(id));
            newAttachment.setProject(project);
        } else if (parent.equals("issue")) {
            Issue issue = issueService.findIssuesById(Integer.parseInt(id));
            newAttachment.setIssue(issue);
        }
        return fileStorageRepository.save(newAttachment);
    }

    public Attachment getFile(Integer fileId) {
        return fileStorageRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File", "id", fileId));
    }

    public void deleteFile(String parent, Integer fileId) {
        Attachment attachment = fileStorageRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File", "id", fileId));
        if (parent.equals("project")) {
            Project project = attachment.getProject();
            project.getAttachments().remove(attachment);
            attachment.setProject(null);
        } else if (parent.equals("issue")) {
            Issue issue = attachment.getIssue();
            issue.getAttachments().remove(attachment);
            attachment.setIssue(null);
        }
        fileStorageRepository.delete(attachment);
    }
}
