package com.example.demo.controllers;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.models.Issue;
import com.example.demo.models.Project;
import com.example.demo.models.User;
import com.example.demo.services.IssueService;
import com.example.demo.services.ProjectService;
import com.example.demo.services.UserService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api")
public class IssueController {

    private final IssueService issueService;
    private final ProjectService projectService;

    @Autowired
    public IssueController(IssueService issueService, ProjectService projectService) {
        this.issueService = issueService;
        this.projectService = projectService;
    }

    // FIXME this view is giving TransientObjectException probably due to the cascade type
    @GetMapping("/{project_id}")
    public ResponseEntity<List<Issue>> getAllIssues(@PathVariable Integer project_id) {
        Project project = projectService.findProjectWithId(project_id);
        if (project == null) {
            throw new ResourceNotFoundException("project", "id", project_id);
        }
        List<Issue> issueList = issueService.findIssuesByProject(project);
        return ResponseEntity.ok(issueList);
    }

    // FIXME this endpoints create new user in the author field although the user exist
    @PostMapping("/{project_id}")
    public ResponseEntity<Issue> createIssue(@PathVariable Integer project_id, @RequestBody Issue issue) {
        Project project = projectService.findProjectWithId(project_id);
        if (project == null) {
            throw new ResourceNotFoundException("project", "id", project_id);
        }
        project.getIssue().add(issue);
        issue.setProject(project);
        issueService.createIssue(issue);
        return ResponseEntity.ok(issue);
    }

    @PutMapping("{project_id}/{issue_id}")
    public ResponseEntity<?> updateIssue(@PathVariable Integer project_id, @PathVariable Integer issue_id, @RequestBody Issue updatedIssue){
        Issue issue = issueService.findIssuesById(issue_id);
        if (issue == null) {
            throw new ResourceNotFoundException("issue", "id", issue_id);
        }
        issueService.updateIssue(project_id, issue, updatedIssue);
        return ResponseEntity.ok(HttpStatus.OK);
    }
    
    @Transactional
    @DeleteMapping("/{project_id}/{issue_id}")
    public ResponseEntity<?> deleteIssue(@PathVariable Integer project_id, @PathVariable Integer issue_id) {
        Project project = projectService.findProjectWithId(project_id);
        if (project == null) {
            throw new ResourceNotFoundException("project", "id", project_id);
        }
        Issue issue = issueService.findIssuesById(issue_id);
        if (issue == null) {
            throw new ResourceNotFoundException("issue", "id", issue_id);
        }
        issueService.deleteIssue(project, issue);
        return ResponseEntity.ok(HttpStatus.OK);
        /*User user=userService.getUserById(project.getUser().getUser_id());
        Authentication authentication= org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if(projectService.findProjectWithId(project_id).getUser().getUsername().equals(authentication.getName())) {
            Issue issue = issueService.findIssuesById(issue_id);
            project.getIssue().remove(issue);
            issue.setProject(null);
            issueService.deleteIssue(issue);
            return new ResponseEntity<>(issue, HttpStatus.OK);
        }
        return null;*/
    }

    @GetMapping("{project_id}/report/get")
    public ResponseEntity<Resource> generateReport(@PathVariable Integer project_id) throws IOException, DocumentException {
        Project project = projectService.findProjectWithId(project_id);
        List<Issue> issues = issueService.findIssuesByProject(project);
        Document document = new Document(PageSize.A4, 25, 25, 25, 25);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, os);
        document.open();
        Paragraph title = new Paragraph("ISSUES FOR " + project.getName() +"\n Total issues: "+issues.size());
        document.add(title);
        PdfPTable table = new PdfPTable(6);
        PdfPTable resolved = new PdfPTable(6);
        PdfPTable unResolved = new PdfPTable(6);
        table.setSpacingBefore(25);
        table.setSpacingAfter(25);
        resolved.setSpacingBefore(25);
        resolved.setSpacingAfter(25);
        unResolved.setSpacingBefore(25);
        unResolved.setSpacingAfter(25);

        BaseColor color = new BaseColor(135,206,250);

        PdfPCell c1 = new PdfPCell(new Phrase("Resolved"));
        int resolvedCounter = 0;
        c1.setBackgroundColor(color);
        table.addCell(c1);
        PdfPCell c2 = new PdfPCell(new Phrase("Unresolved"));
        int unResolvedCounter = 0;
        c2.setBackgroundColor(color);
        table.addCell(c2);
        PdfPCell c3 = new PdfPCell(new Phrase("In progress"));
        int inProgressCounter = 0;
        c3.setBackgroundColor(color);
        table.addCell(c3);
        PdfPCell c4 = new PdfPCell(new Phrase("Top performer"));
        c4.setBackgroundColor(color);
        table.addCell(c4);
        PdfPCell c5 = new PdfPCell(new Phrase("Frontend label"));
        c5.setBackgroundColor(color);
        int frontEndCounter= 0;
        table.addCell(c5);
        PdfPCell c6 = new PdfPCell(new Phrase("Backend label"));
        c6.setBackgroundColor(color);
        int backEndCounter = 0;
        table.addCell(c6);

        for (Issue issue : issues){
            if (issue.getStatus().equals("Resolved")){
                resolvedCounter++;
            }
            else if (issue.getStatus().equals("In Progress")){
                inProgressCounter++;
            }
            else unResolvedCounter++;

            for (String tag: issue.getTag()){
                if (tag.equals("Frontend")){
                    frontEndCounter++;
                }
                else backEndCounter++;
            }
        }

            table.addCell(String.valueOf(resolvedCounter));
            table.addCell(String.valueOf(unResolvedCounter));
            table.addCell(String.valueOf(inProgressCounter));
            table.addCell(String.valueOf(issues.get(0).getCreatedBy()));
            table.addCell(String.valueOf(frontEndCounter));
            table.addCell(String.valueOf(backEndCounter));


        Paragraph resolvedTitle = new Paragraph("Issue resolved");
        PdfPCell rC1 = new PdfPCell(new Phrase("Issue ID"));
        rC1.setBackgroundColor(color);
        resolved.addCell(rC1);
        PdfPCell rC2 = new PdfPCell(new Phrase("Title"));
        rC2.setBackgroundColor(color);
        resolved.addCell(rC2);
        PdfPCell rC3 = new PdfPCell(new Phrase("Priority"));
        rC3.setBackgroundColor(color);
        resolved.addCell(rC3);
        PdfPCell rC4 = new PdfPCell(new Phrase("Tag"));
        rC4.setBackgroundColor(color);
        resolved.addCell(rC4);
        PdfPCell rC5 = new PdfPCell(new Phrase("CreatedBy"));
        rC5.setBackgroundColor(color);
        resolved.addCell(rC5);
        PdfPCell rC6 = new PdfPCell(new Phrase("Assignee"));
        rC6.setBackgroundColor(color);
        resolved.addCell(rC6);

        for (Issue issue : issues){
            if (issue.getStatus().equalsIgnoreCase("Resolved")){
                resolved.addCell(String.valueOf(issue.getIssue_id()));
                resolved.addCell(String.valueOf(issue.getTitle()));
                resolved.addCell(String.valueOf(issue.getPriority()));
                resolved.addCell(String.valueOf(issue.getTag().toString()));
                resolved.addCell(String.valueOf(issue.getCreatedBy()));
                resolved.addCell(String.valueOf(issue.getAssignee()));
            }
        }

        Paragraph unResolvedTitle = new Paragraph("Unresolved Issues");
        PdfPCell uC1 = new PdfPCell(new Phrase("Issue ID"));
        uC1.setBackgroundColor(color);
        unResolved.addCell(uC1);
        PdfPCell uC2 = new PdfPCell(new Phrase("Title"));
        uC2.setBackgroundColor(color);
        unResolved.addCell(uC2);
        PdfPCell uC3 = new PdfPCell(new Phrase("Priority"));
        uC3.setBackgroundColor(color);
        unResolved.addCell(uC3);
        PdfPCell uC4 = new PdfPCell(new Phrase("Tag"));
        uC4.setBackgroundColor(color);
        unResolved.addCell(uC4);
        PdfPCell uc5 = new PdfPCell(new Phrase("CreatedBy"));
        uc5.setBackgroundColor(color);
        unResolved.addCell(uc5);
        PdfPCell uC6 = new PdfPCell(new Phrase("Assignee"));
        uC6.setBackgroundColor(color);
        unResolved.addCell(uC6);

        for (Issue issue : issues){
            if (!issue.getStatus().equalsIgnoreCase("Resolved")){
                unResolved.addCell(String.valueOf(issue.getIssue_id()));
                unResolved.addCell(String.valueOf(issue.getTitle()));
                unResolved.addCell(String.valueOf(issue.getPriority()));
                unResolved.addCell(String.valueOf(issue.getTag().toString()));
                unResolved.addCell(String.valueOf(issue.getCreatedBy()));
                unResolved.addCell(String.valueOf(issue.getAssignee()));
            }
        }

        document.add(table);
        document.add(resolvedTitle);
        document.add(resolved);
        document.add(unResolvedTitle);
        document.add(unResolved);
        document.close();
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ProductPdfReport.pdf");

        ResponseEntity<Resource> response = new ResponseEntity<>(new InputStreamResource(is), headers,
                HttpStatus.OK);

        return response;
    }

}
