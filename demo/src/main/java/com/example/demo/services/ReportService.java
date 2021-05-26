package com.example.demo.services;

import com.example.demo.models.Issue;
import com.example.demo.models.Project;
import com.example.demo.repository.IssueRepository;
import com.example.demo.repository.ProjectRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private ProjectRepository projectRepository;

    public void exportReport(Integer project_id) throws FileNotFoundException, JRException {
        Project project = projectRepository.findProjectById(project_id);
        List<Issue> issues = issueRepository.findByProject(project);
        File file = ResourceUtils.getFile("classpath:report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
        JRBeanCollectionDataSource datasource = new JRBeanCollectionDataSource(issues);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("projectName", project.getName());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, datasource);
        JasperExportManager.exportReportToHtmlFile(jasperPrint,"report.html");
    }
}
