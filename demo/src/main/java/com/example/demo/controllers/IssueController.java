package com.example.demo.controllers;

import static com.example.demo.controllers.AuthController.referUser;
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
import com.sun.xml.bind.v2.TODO;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.*;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;

@Controller
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

    // FIXME this view is giving TransientObjectException probably due to the cascade type
    @GetMapping("/{project_id}")
    public ResponseEntity<List<Issue>> getAllIssues(@PathVariable Integer project_id,
                                                    @RequestParam(defaultValue = "timestamp,desc") String[] sort,
                                                    @RequestParam(defaultValue = "none,none") String[] filter) {
        Project project = projectService.findProjectWithId(project_id);
        if (project == null) {
            throw new ResourceNotFoundException("project", "id", project_id);
        }
        List<Issue> issueList = issueService.findIssuesByProjectWithSortAndFilter(sort, filter, project);
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
        Stack<Issue> issueStack=new Stack<>();
        issueStack.push(issue);
        HashMap<Integer,Stack<Issue>> issueMap=new HashMap<>();
        issueMap.put(issue.getIssueId(), issueStack);
        referUser.getIssueUndo().put(project_id, issueMap);
        return ResponseEntity.ok(issue);
    }

    @GetMapping("issue/{issue_id}")
    public ResponseEntity<Issue> getIssue(@PathVariable Integer issue_id) {
        Issue issue = issueService.findIssuesById(issue_id);
        return ResponseEntity.ok(issue);
    }

    @PutMapping("{project_id}/{issue_id}")
    public ResponseEntity<?> updateIssue(@PathVariable Integer project_id, @PathVariable Integer issue_id, @RequestBody Issue updatedIssue) throws CloneNotSupportedException {
        Issue issue = issueService.findIssuesById(issue_id);
        Issue referIssue=(Issue)issue.clone();
        if(referUser.getIssueUndo()==null || !referUser.getIssueUndo().containsKey(project_id)) {
            Stack<Issue> issueStack=new Stack<>();
            issueStack.push(referIssue);
            HashMap<Integer,Stack<Issue>> issueMap=new HashMap<>();
            issueMap.put(issue_id, issueStack);
            referUser.getIssueUndo().put(project_id, issueMap);
        }
        else if(!referUser.getIssueUndo().get(project_id).containsKey(issue_id)) {
            Stack<Issue> issueStack=new Stack<>();
            issueStack.push(referIssue);
            referUser.getIssueUndo().get(project_id).put(issue_id, issueStack);
        }
        else if(referUser.getIssueUndo().get(project_id).get(issue_id).isEmpty()) {
            referUser.getIssueUndo().get(project_id).get(issue_id).push(referIssue);
        }
        if (issue == null) {
            throw new ResourceNotFoundException("issue", "id", issue_id);
        }
        issueService.updateIssue(project_id, issue, updatedIssue);
        referUser.getIssueUndo().get(project_id).get(issue_id).push(updatedIssue);
        if(referUser.getIssueRedo()!=null && referUser.getIssueRedo().containsKey(project_id)) {
            if(referUser.getIssueRedo().get(project_id).containsKey(issue_id)) {
                referUser.getIssueRedo().get(project_id).get(issue_id).clear();
            }
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }
    
    public void updateTheIssue(Integer project_id, Integer issue_id, Issue updatedIssue) {
//        if(referUser.getIssueUndo()==null || !referUser.getIssueUndo().containsKey(project_id)) {
//            Stack<Issue> issueStack=new Stack<>();
//            issueStack.push(issueService.findIssuesById(issue_id));
//            HashMap<Integer,Stack<Issue>> issueMap=new HashMap<>();
//            issueMap.put(issue_id, issueStack);
//            referUser.getIssueUndo().put(project_id, issueMap);
//        }
        Issue issue = issueService.findIssuesById(issue_id);
        if (issue == null) {
            throw new ResourceNotFoundException("issue", "id", issue_id);
        }
        issueService.updateIssue(project_id, issue, updatedIssue);
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
        if(referUser.getIssueUndo().containsKey(project_id) && referUser.getIssueUndo().get(project_id).containsKey(issue_id)) {
            referUser.getIssueUndo().get(project_id).remove(issue_id);
        }
        if(referUser.getIssueRedo().containsKey(project_id) && referUser.getIssueRedo().get(project_id).containsKey(issue_id)) {
            referUser.getIssueRedo().get(project_id).remove(issue_id);
        }
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
    
    @Transactional
    @GetMapping("/issue/{issue_id}/history")
    public ResponseEntity<?> getHistory(@PathVariable Integer issue_id) {
        Issue issue=issueService.findIssuesById(issue_id);
        //if(referUser.getUsername().equals(issue.getCreatedBy()) || referUser.getUsername.equals(issue.getAssignee())) {
            return ResponseEntity.ok(issueService.getHistory(issue_id));
        //}
        //return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    @Transactional
    @GetMapping("/{username}/issue/history")
    public ResponseEntity<?> getOwnHistory(@PathVariable String username) {
        return ResponseEntity.ok(issueService.getOwnHistory(username));
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    @GetMapping("/issue/history")
    public ResponseEntity<?> getAllHistory() {
        return ResponseEntity.ok(issueService.getAllHistory());
    }
    
    @GetMapping("/{project_id}/{issue_id}/issue/undo")
    public ResponseEntity<HashMap<?, ?>> undoIssue(@PathVariable Integer project_id, @PathVariable Integer issue_id) {
        try{
            if(referUser.getIssueUndo().get(project_id).get(issue_id).isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            if(referUser.getIssueUndo().containsKey(project_id) && !referUser.getIssueUndo().get(project_id).get(issue_id).isEmpty()) {
                Issue undoIssue=referUser.getIssueUndo().get(project_id).get(issue_id).pop();
                if(!referUser.getIssueRedo().containsKey(project_id)) {
                    Stack<Issue> issueStack=new Stack<>();
                    issueStack.push(undoIssue);
                    HashMap<Integer,Stack<Issue>> issueMap=new HashMap<>();
                    issueMap.put(issue_id, issueStack);
                    referUser.getIssueRedo().put(project_id, issueMap);
                }
                else if(!referUser.getIssueRedo().get(project_id).containsKey(issue_id)) {
                    Stack<Issue> issueStack=new Stack<>();
                    issueStack.push(undoIssue);
                    referUser.getIssueRedo().get(project_id).put(issue_id, issueStack);
                }
                else if(referUser.getIssueRedo().get(project_id).containsKey(issue_id)) {
                    referUser.getIssueRedo().get(project_id).get(issue_id).push(undoIssue);
                }
                updateTheIssue(project_id, issue_id, referUser.getIssueUndo().get(project_id).get(issue_id).peek());
                Issue issue=issueService.findIssuesById(issue_id);
                HashMap<String,Object> map=new HashMap<>();
                map.put("project_id", project_id);
                map.put("issue_id", issue_id);
                map.put("issue", issue);
                return ResponseEntity.ok(map);
            }
        }
        catch(EmptyStackException | NullPointerException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    @GetMapping("/{project_id}/{issue_id}/issue/redo")
    public ResponseEntity<?> redoIssue(@PathVariable Integer project_id, @PathVariable Integer issue_id) {
        if(referUser.getIssueRedo().get(project_id).get(issue_id).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if(referUser.getIssueRedo().containsKey(project_id) && referUser.getIssueRedo().get(project_id).containsKey(issue_id) && !referUser.getIssueRedo().get(project_id).get(issue_id).isEmpty()) {
            Issue redoIssue=referUser.getIssueRedo().get(project_id).get(issue_id).pop();
            referUser.getIssueUndo().get(project_id).get(issue_id).push(redoIssue);
            updateTheIssue(project_id, issue_id, redoIssue);
            Issue issue=issueService.findIssuesById(issue_id);
            HashMap<String,Object> map=new HashMap<>();
            map.put("project_id", project_id);
            map.put("issue_id", issue_id);
            map.put("issue", issue);
            return ResponseEntity.ok(map);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{project_id}/charts")
    public String getAllEmployee(@PathVariable Integer project_id, Model model) {
        List<Issue> issues = issueService.findIssuesByProject(projectService.findProjectWithId(project_id));
        List<String> tagList = new ArrayList<>();
        List<Integer> tagCounterList = new ArrayList<>();
        List<String> statusList = new ArrayList<>();
        List<Integer> statusCounterList = new ArrayList<>();
        Integer frontend = 0, backend = 0, firstBug = 0, enhancement = 0, suggestion = 0;
        Integer resolved = 0, open = 0, closed = 0, reopened = 0, inProgress = 0;

        tagList.add("Frontend");
        tagList.add("Backend");
        tagList.add("First bug");
        tagList.add("Enhancement");
        tagList.add("Suggestion");

        statusList.add("Resolved");
        statusList.add("Reopened");
        statusList.add("Open");
        statusList.add("Closed");
        statusList.add("In progress");

        for (Issue issue : issues) {
            int counter = issue.getTag().size();
            for (int i = 0; i < counter; i++) {
                if (issue.getTag().get(i).equalsIgnoreCase("Frontend")) {
                    frontend++;
                }
                if (issue.getTag().get(i).equalsIgnoreCase("Backend")) {
                    backend++;
                }
                if (issue.getTag().get(i).equalsIgnoreCase("First Bug")) {
                    firstBug++;
                }
                if (issue.getTag().get(i).equalsIgnoreCase("Enhancement")) {
                    enhancement++;
                }
                if (issue.getTag().get(i).equalsIgnoreCase("Suggestion")) {
                    suggestion++;
                }
            }
        }

        tagCounterList.add(frontend);
        tagCounterList.add(backend);
        tagCounterList.add(firstBug);
        tagCounterList.add(enhancement);
        tagCounterList.add(suggestion);

        for (Issue issue : issues) {
            if (issue.getStatus().equalsIgnoreCase("Resolved")) {
                resolved++;
            } else if (issue.getStatus().equalsIgnoreCase("Reopened")) {
                reopened++;
            } else if (issue.getStatus().equalsIgnoreCase("Open")) {
                open++;
            } else if (issue.getStatus().equalsIgnoreCase("closed")) {
                closed++;
            } else {
                inProgress++;
            }
        }

        statusCounterList.add(resolved);
        statusCounterList.add(reopened);
        statusCounterList.add(open);
        statusCounterList.add(closed);
        statusCounterList.add(inProgress);

        List<PieChart> pieCharts = new ArrayList<>();
        for (int i = 0; i < statusList.size(); i++) {
            PieChart pieChart = new PieChart(statusList.get(i), statusCounterList.get(i));
            pieCharts.add(pieChart);
        }

        List<String> headers = Arrays.asList("ID", "Title", "Status", "Priority", "Tag", "Created by", "Assignee");
        List<Map<String, Object>> rows = new ArrayList<>();
        int counter = 1;
        for (Issue issue : issues) {
            rows.add(Map.of("ID", String.valueOf(counter++), "Title", issue.getTitle(), "Status", issue.getStatus(), "Priority", String.valueOf(issue.getPriority()), "Tag", issue.getTag().toString().replace("[", "").replace("]", ""), "Created by", issue.getCreatedBy(), "Assignee", issue.getAssignee()));
        }

        int days = LocalDate.now().lengthOfMonth();
        List<Integer> totalDays = new ArrayList<>();
        for (int i = 1; i <= days; i++) {
            totalDays.add(i);
        }

        List<LocalDate> allDates = new ArrayList<>();
        Month currentMonth = LocalDate.now().getMonth();
        int currentYear = LocalDate.now().getYear();
        YearMonth ym = YearMonth.of(currentYear, currentMonth);
        LocalDate firstOfMonth = ym.atDay(1);
        LocalDate firstOfFollowingMonth = ym.plusMonths(1).atDay(1);
        firstOfMonth.datesUntil(firstOfFollowingMonth).forEach(allDates::add);

        List<Integer> issueCounter = new ArrayList<>();
        for (int i = 0; i < allDates.size(); i++) {
            int isCounter = 0;
            boolean found = false;
            for (Issue issue : issues) {
                if (issue.getTimestamp().equals(Date.from(allDates.get(i).atStartOfDay(ZoneId.systemDefault()).toInstant()))){
                    found = true;
                    isCounter++;
                }
            }
            if (found){
                issueCounter.add(isCounter);
            }
            else issueCounter.add(0);
        }

        List<Integer> issueCumulativeCounter = new ArrayList<>();

        int cumulativeCounter = 0;
        for (int i = 0; i < days; i++) {
            cumulativeCounter += issueCounter.get(i);
            issueCumulativeCounter.add(cumulativeCounter);
        }

        List<String> assignee = new ArrayList<>();
        List<Integer> numberOfIssueSolved = new ArrayList<>();
        for (Issue issue: issues){
            if (!assignee.contains(issue.getAssignee())){
                assignee.add(issue.getAssignee());
            }
        }

        for (String name : assignee){
            int count = 0;
            for (Issue issue : issues){
                if (issue.getAssignee().equals(name)){
                    count++;
                }
            }
            numberOfIssueSolved.add(count);
        }

        HashMap<String, Integer> performerList = new HashMap<>();
        for (int i = 0; i < assignee.size(); i++) {
            performerList.put(assignee.get(i), numberOfIssueSolved.get(i));
        }

        //TODO ranking for top performer
        Map<String, Integer> ranking = issueService.sortByValue(performerList);


        model.addAttribute("cumulativeCounter", issueCumulativeCounter);
        model.addAttribute("issueCounter", issueCounter);
        model.addAttribute("days", totalDays);
        model.addAttribute("tags", tagList);
        model.addAttribute("counter", tagCounterList);
        model.addAttribute("data", pieCharts);
        model.addAttribute("headers", headers);
        model.addAttribute("rows", rows);
        return "chart";

    }

    @GetMapping("/{project_id}/charts/data")
    public ResponseEntity<?> getChartData(@PathVariable Integer project_id) {
        List<Issue> issues = issueService.findIssuesByProject(projectService.findProjectWithId(project_id));
        List<String> tagList = new ArrayList<>();
        List<Integer> tagCounterList = new ArrayList<>();
        List<String> statusList = new ArrayList<>();
        List<Integer> statusCounterList = new ArrayList<>();
        Integer frontend = 0, backend = 0, firstBug = 0, enhancement = 0, suggestion = 0;
        Integer resolved = 0, open = 0, closed = 0, reopened = 0, inProgress = 0;

        tagList.add("Frontend");
        tagList.add("Backend");
        tagList.add("First bug");
        tagList.add("Enhancement");
        tagList.add("Suggestion");

        statusList.add("Resolved");
        statusList.add("Reopened");
        statusList.add("Open");
        statusList.add("Closed");
        statusList.add("In progress");

        for (Issue issue : issues) {
            int counter = issue.getTag().size();
            for (int i = 0; i < counter; i++) {
                if (issue.getTag().get(i).equalsIgnoreCase("Frontend")) {
                    frontend++;
                }
                if (issue.getTag().get(i).equalsIgnoreCase("Backend")) {
                    backend++;
                }
                if (issue.getTag().get(i).equalsIgnoreCase("First Bug")) {
                    firstBug++;
                }
                if (issue.getTag().get(i).equalsIgnoreCase("Enhancement")) {
                    enhancement++;
                }
                if (issue.getTag().get(i).equalsIgnoreCase("Suggestion")) {
                    suggestion++;
                }
            }
        }

        tagCounterList.add(frontend);
        tagCounterList.add(backend);
        tagCounterList.add(firstBug);
        tagCounterList.add(enhancement);
        tagCounterList.add(suggestion);

        for (Issue issue : issues) {
            if (issue.getStatus().equalsIgnoreCase("Resolved")) {
                resolved++;
            } else if (issue.getStatus().equalsIgnoreCase("Reopened")) {
                reopened++;
            } else if (issue.getStatus().equalsIgnoreCase("Open")) {
                open++;
            } else if (issue.getStatus().equalsIgnoreCase("closed")) {
                closed++;
            } else {
                inProgress++;
            }
        }

        statusCounterList.add(resolved);
        statusCounterList.add(reopened);
        statusCounterList.add(open);
        statusCounterList.add(closed);
        statusCounterList.add(inProgress);

        List<PieChart> pieCharts = new ArrayList<>();
        for (int i = 0; i < statusList.size(); i++) {
            PieChart pieChart = new PieChart(statusList.get(i), statusCounterList.get(i));
            pieCharts.add(pieChart);
        }

        List<String> headers = Arrays.asList("ID", "Title", "Status", "Priority", "Tag", "Created by", "Assignee");
        List<Map<String, Object>> rows = new ArrayList<>();
        int counter = 1;
        for (Issue issue : issues) {
            rows.add(Map.of("ID", String.valueOf(counter++), "Title", issue.getTitle(), "Status", issue.getStatus(), "Priority", String.valueOf(issue.getPriority()), "Tag", issue.getTag().toString().replace("[", "").replace("]", ""), "Created by", issue.getCreatedBy(), "Assignee", issue.getAssignee()));
        }

        int days = LocalDate.now().lengthOfMonth();
        List<Integer> totalDays = new ArrayList<>();
        for (int i = 1; i <= days; i++) {
            totalDays.add(i);
        }

        List<LocalDate> allDates = new ArrayList<>();
        Month currentMonth = LocalDate.now().getMonth();
        int currentYear = LocalDate.now().getYear();
        YearMonth ym = YearMonth.of(currentYear, currentMonth);
        LocalDate firstOfMonth = ym.atDay(1);
        LocalDate firstOfFollowingMonth = ym.plusMonths(1).atDay(1);
        firstOfMonth.datesUntil(firstOfFollowingMonth).forEach(allDates::add);

        List<Integer> issueCounter = new ArrayList<>();
        for (int i = 0; i < allDates.size(); i++) {
            int isCounter = 0;
            boolean found = false;
            for (Issue issue : issues) {
                if (issue.getTimestamp().equals(Date.from(allDates.get(i).atStartOfDay(ZoneId.systemDefault()).toInstant()))){
                    found = true;
                    isCounter++;
                }
            }
            if (found){
                issueCounter.add(isCounter);
            }
            else issueCounter.add(0);
        }

        List<Integer> issueCumulativeCounter = new ArrayList<>();

        int cumulativeCounter = 0;
        for (int i = 0; i < days; i++) {
            cumulativeCounter += issueCounter.get(i);
            issueCumulativeCounter.add(cumulativeCounter);
        }

        HashMap<String, Object> response = new HashMap<>();

        response.put("cumulativeCounter", issueCumulativeCounter);
        response.put("issueCounter", issueCounter);
        response.put("days", totalDays);
        response.put("tags", tagList);
        response.put("counter", tagCounterList);
        response.put("data", pieCharts);
        response.put("headers", headers);
        response.put("rows", rows);

        return ResponseEntity.ok(response);

    }

    static class PieChart {
        String name;
        Integer y;

        public PieChart(String name, Integer value) {
            this.name = name;
            this.y = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getY() {
            return y;
        }

        public void setY(Integer y) {
            this.y = y;
        }
    }
}
