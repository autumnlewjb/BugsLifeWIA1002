package com.example.demo.controllers;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.models.Issue;
import com.example.demo.models.Project;
import com.example.demo.services.IssueService;
import com.example.demo.services.ProjectService;
import com.example.demo.services.ReportService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import net.sf.jasperreports.engine.JRException;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import javax.transaction.Transactional;

@RestController
@RequestMapping("/api")
public class IssueController {

    private final IssueService issueService;
    private final ProjectService projectService;
    private final ReportService reportService;

    @Autowired
    public IssueController(IssueService issueService, ProjectService projectService, ReportService reportService) {
        this.issueService = issueService;
        this.projectService = projectService;
        this.reportService = reportService;
    }

    private PdfPCell getCell(int cm) {
        PdfPCell cell = new PdfPCell();
        cell.setColspan(cm);
        cell.setUseAscender(true);
        cell.setUseDescender(true);
        Paragraph p = new Paragraph(
                String.format("%smm", 10 * cm),
                new Font(Font.FontFamily.HELVETICA, 8));
        p.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(p);
        return cell;
    }

    // FIXME this view is giving TransientObjectException probably due to the cascade type
    @GetMapping("/{project_id}")
    public ResponseEntity<List<Issue>> getAllIssues(@PathVariable Integer project_id,
                                                    @RequestParam(defaultValue = "timestamp,desc") String sort,
                                                    @RequestParam(defaultValue = "none") String filter) {
        Project project = projectService.findProjectWithId(project_id);
        if (project == null) {
            throw new ResourceNotFoundException("project", "id", project_id);
        }
        List<Issue> issueList = issueService.findIssuesWithSortAndFilter(sort, filter, project);
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
        BaseColor color = new BaseColor(135,206,250);
        Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        Document document = new Document(PageSize.A4, 25, 10, 10, 10);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, os);
        document.open();
        Paragraph title = new Paragraph("Report for " + project.getName() +"\nTotal issues: "+issues.size(), headFont);
        document.add(title);
        PdfPTable table = new PdfPTable(6);
        PdfPTable resolved = new PdfPTable(7);
        PdfPTable unResolved = new PdfPTable(7);
        table.setSpacingBefore(10);
        table.setSpacingAfter(10);
        resolved.setSpacingBefore(10);
        resolved.setSpacingAfter(10);
        unResolved.setSpacingBefore(10);
        unResolved.setSpacingAfter(10);


        PdfPCell c1 = new PdfPCell(new Phrase("Resolved", headFont));
        int resolvedCounter = 0;
        c1.setBackgroundColor(color);
        table.addCell(c1);
        PdfPCell c2 = new PdfPCell(new Phrase("Unresolved", headFont));
        int unResolvedCounter = 0;
        c2.setBackgroundColor(color);
        table.addCell(c2);
        PdfPCell c3 = new PdfPCell(new Phrase("In progress", headFont));
        int inProgressCounter = 0;
        c3.setBackgroundColor(color);
        table.addCell(c3);
        PdfPCell c4 = new PdfPCell(new Phrase("Top performer", headFont));
        c4.setBackgroundColor(color);
        table.addCell(c4);
        PdfPCell c5 = new PdfPCell(new Phrase("Frontend label", headFont));
        c5.setBackgroundColor(color);
        int frontEndCounter= 0;
        table.addCell(c5);
        PdfPCell c6 = new PdfPCell(new Phrase("Backend label", headFont));
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


        Paragraph resolvedTitle = new Paragraph("Issue resolved", headFont);
        PdfPCell rC1 = new PdfPCell(new Phrase("Issue ID", headFont));
        rC1.setBackgroundColor(color);
        resolved.addCell(rC1);
        PdfPCell rC2 = new PdfPCell(new Phrase("Title", headFont));
        rC2.setBackgroundColor(color);
        rC2.setColspan(2);
        resolved.addCell(rC2);
        PdfPCell rC3 = new PdfPCell(new Phrase("Priority", headFont));
        rC3.setBackgroundColor(color);
        resolved.addCell(rC3);
        PdfPCell rC4 = new PdfPCell(new Phrase("Tag", headFont));
        rC4.setBackgroundColor(color);
        resolved.addCell(rC4);
        PdfPCell rC5 = new PdfPCell(new Phrase("Created by", headFont));
        rC5.setBackgroundColor(color);
        resolved.addCell(rC5);
        PdfPCell rC6 = new PdfPCell(new Phrase("Assignee", headFont));
        rC6.setBackgroundColor(color);
        resolved.addCell(rC6);


        Paragraph unResolvedTitle = new Paragraph("Unresolved Issues", headFont);
        PdfPCell uC1 = new PdfPCell(new Phrase("Issue ID", headFont));
        uC1.setBackgroundColor(color);
        unResolved.addCell(uC1);
        PdfPCell uC2 = new PdfPCell(new Phrase("Title", headFont));
        uC2.setBackgroundColor(color);
        uC2.setColspan(2);
        unResolved.addCell(uC2);
        PdfPCell uC3 = new PdfPCell(new Phrase("Priority", headFont));
        uC3.setBackgroundColor(color);
        unResolved.addCell(uC3);
        PdfPCell uC4 = new PdfPCell(new Phrase("Tag", headFont));
        uC4.setBackgroundColor(color);
        unResolved.addCell(uC4);
        PdfPCell uc5 = new PdfPCell(new Phrase("Created by", headFont));
        uc5.setBackgroundColor(color);
        unResolved.addCell(uc5);
        PdfPCell uC6 = new PdfPCell(new Phrase("Assignee", headFont));
        uC6.setBackgroundColor(color);
        unResolved.addCell(uC6);

        int counter = 1;
        PdfPCell tempCell = new PdfPCell();
        tempCell.setColspan(2);
        for (Issue issue : issues){
            if (issue.getStatus().equalsIgnoreCase("Resolved")){
                resolved.addCell(String.valueOf(counter++));
                tempCell.setPhrase(new Phrase(String.valueOf(issue.getTitle())));
                resolved.addCell(tempCell);
                resolved.addCell(String.valueOf(issue.getPriority()));
                resolved.addCell(String.valueOf(issue.getTag().toString()));
                resolved.addCell(String.valueOf(issue.getCreatedBy()));
                resolved.addCell(String.valueOf(issue.getAssignee()));
            }
            else {
                unResolved.addCell(String.valueOf(counter++));
                tempCell.setPhrase(new Phrase(String.valueOf(issue.getTitle())));
                unResolved.addCell(tempCell);
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

    @GetMapping("/{project_id}/generateReport")
    public String exportReport(@PathVariable Integer project_id) throws JRException, FileNotFoundException {
        return reportService.exportReport(project_id);
    }
}
