package com.example.demo.controllers;

import static com.example.demo.controllers.AuthController.referUser;
import java.util.List;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.models.Comment;
import com.example.demo.models.Issue;
import com.example.demo.services.CommentService;

import com.example.demo.services.IssueService;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Stack;
import javax.transaction.Transactional;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.CacheControl;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
public class CommentController {
    
    @Value("${spring.datasource.url}")
    private String database;
    @Value("${spring.datasource.username}")
    private String databaseUsername;
    @Value("${spring.datasource.password}")
    private String databasePassword;
    private final CommentService commentService;
    private final IssueService issueService;

    @Autowired
    public CommentController(CommentService commentService, IssueService issueService) {
        this.commentService = commentService;
        this.issueService = issueService;
    }

    @GetMapping("/{project_id}/{issue_id}")
    public ResponseEntity<List<Comment>> getCommentsByIssue(@PathVariable Integer issue_id) {
        Issue issue = issueService.findIssuesById(issue_id);
        if (issue == null) {
            throw new ResourceNotFoundException("issue", "id", issue_id);
        }
        List<Comment> commentList=commentService.findCommentsByIssue(issue);
        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(100, TimeUnit.SECONDS)).body(commentList);
    }

    @PostMapping("{project_id}/{issue_id}")
    public ResponseEntity<Comment> createComments(@PathVariable Integer issue_id, @RequestBody Comment comment) {
        Issue issue = issueService.findIssuesById(issue_id);
        if (issue == null) {
            throw new ResourceNotFoundException("issue", "id", issue_id);
        }
        issue.getComment().add(comment);
        comment.setIssue(issue);
        commentService.createComments(comment);
        Stack<Comment> commentStack=new Stack<>();
        commentStack.push(comment);
        if(!referUser.getCommentUndo().containsKey(issue_id)) {
            HashMap<Integer,Stack<Comment>> commentMap=new HashMap<>();
            commentMap.put(comment.getComment_id(), commentStack);
            referUser.getCommentUndo().put(issue_id, commentMap);
        }
        else if(!referUser.getCommentUndo().get(issue_id).containsKey(comment.getComment_id())) {
            referUser.getCommentUndo().get(issue_id).put(comment.getComment_id(), commentStack);
        }
        return ResponseEntity.ok(comment);
    }

    @PutMapping("/{project_id}/{issue_id}/{comment_id}")
    public ResponseEntity<?> updateComment( @PathVariable Integer issue_id, @PathVariable Integer comment_id, @RequestBody Comment updatedComment) throws CloneNotSupportedException {
        Comment comment = commentService.findCommentById(comment_id);
        Comment referComment=(Comment)comment.clone();
        if(referUser.getCommentUndo()==null || !referUser.getCommentUndo().containsKey(issue_id)) {
            Stack<Comment> commentStack=new Stack<>();
            commentStack.push(referComment);
            HashMap<Integer,Stack<Comment>> commentMap=new HashMap<>();
            commentMap.put(comment_id, commentStack);
            referUser.getCommentUndo().put(issue_id, commentMap);
        }
        else if(!referUser.getCommentUndo().get(issue_id).containsKey(comment_id)) {
            Stack<Comment> commentStack=new Stack<>();
            commentStack.push(referComment);
            referUser.getCommentUndo().get(issue_id).put(comment_id, commentStack);
        }
        else if(referUser.getCommentUndo().get(issue_id).get(comment_id).isEmpty()) {
            referUser.getCommentUndo().get(issue_id).get(comment_id).push(referComment);
        }
        if (comment == null) {
            throw new ResourceNotFoundException("comment", "id", comment_id);
        }
        commentService.updateComment(issue_id, comment, updatedComment);
        referUser.getCommentUndo().get(issue_id).get(comment_id).push(updatedComment);
        if(referUser.getCommentRedo()!=null && referUser.getCommentRedo().containsKey(issue_id)) {
            if(referUser.getCommentRedo().get(issue_id).containsKey(comment_id)) {
                referUser.getCommentRedo().get(issue_id).get(comment_id).clear();
            }
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }
    
    public void updateTheComment( @PathVariable Integer issue_id, @PathVariable Integer comment_id, @RequestBody Comment updatedComment){
        Comment comment = commentService.findCommentById(comment_id);
        if (comment == null) {
            throw new ResourceNotFoundException("comment", "id", comment_id);
        }
        commentService.updateComment(issue_id, comment, updatedComment);
    }

    @DeleteMapping("{project_id}/{issue_id}/{comment_id}")
    public ResponseEntity<?> deleteComment(@PathVariable Integer issue_id, @PathVariable Integer comment_id){
        Issue issue=issueService.findIssuesById(issue_id);
        if (issue == null) {
            throw new ResourceNotFoundException("issue", "id", issue_id);
        }
        Comment comment=commentService.findCommentById(comment_id);
        if (comment == null) {
            throw new ResourceNotFoundException("comment", "id", comment_id);
        }
        commentService.deleteComment(issue, comment);
        if(referUser.getCommentUndo().containsKey(issue_id) && referUser.getCommentUndo().get(issue_id).containsKey(comment_id)) {
            referUser.getCommentUndo().get(issue_id).remove(comment_id);
        }
        if(referUser.getCommentRedo().containsKey(issue_id) && referUser.getCommentRedo().get(issue_id).containsKey(comment_id)) {
            referUser.getCommentRedo().get(issue_id).remove(comment_id);
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }
    
    //Get comment history for specific Comment
    @Transactional
    @GetMapping("/comment/{comment_id}/history")
    public ResponseEntity<?> getHistory(@PathVariable Integer comment_id) {
        Comment comment=commentService.findCommentById(comment_id);
        //if(referUser.getUsername().equals(comment.getUser())) {
            return ResponseEntity.ok(commentService.getHistory(comment_id));
        //}
        //return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    /*
    //Alternative querying if got problem later with hibernate enver foreign keys relation
    //Get all comment history by the User in a specific Issue
    @Transactional
    @GetMapping("/issue/{issue_id}/comment/history")
    public ResponseEntity<?> getIssueHistory(@PathVariable Integer issue_id) {
        try{
            List list=new ArrayList<>();
            Connection connection = DriverManager.getConnection(database, databaseUsername, databasePassword);
            Statement statement = connection.createStatement();
            String num=String.valueOf(issue_id);
            String queryString="SELECT * FROM db.comment_aud WHERE issue_id="+num+";";
            PreparedStatement preparedStatement=connection.prepareStatement(queryString);
            ResultSet resultSet=preparedStatement.executeQuery();
            
            ResultSetMetaData metaData = resultSet.getMetaData();
            int numOfColumns = metaData.getColumnCount();
            List<String> columnNames = new ArrayList<>();
            for (int i = 1; i <= numOfColumns; i++) {
                columnNames.add(metaData.getColumnName(i));
            }
            
            while(resultSet.next()) {
                JSONObject obj = new JSONObject();
                for (int i = 1; i <= numOfColumns; i++) {
                    String key = columnNames.get(i - 1);
                    String value = resultSet.getString(i);
                    obj.put(key, value);
                }
                list.add(obj);
            }
            return ResponseEntity.ok(list);
        }
        catch(SQLException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    */
    
    //Get all comment history by the User in a specific Issue
    @Transactional
    @GetMapping("/issue/{issue_id}/comment/history")
    public ResponseEntity<?> getIssueHistory(@PathVariable Integer issue_id) {
        return ResponseEntity.ok(commentService.getIssueHistory(issue_id));
    }
    
    //Get all comment history by the User
    @Transactional
    @GetMapping("/{username}/comment/history")
    public ResponseEntity<?> getOwnHistory(@PathVariable String username) {
        return ResponseEntity.ok(commentService.getOwnHistory(username));
    }
    
    //Get all comment history
    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    @GetMapping("/comment/history")
    public ResponseEntity<?> getAllHistory() {
        return ResponseEntity.ok(commentService.getAllHistory());
    }
    
    @GetMapping("/{issue_id}/{comment_id}/comment/undo")
    public ResponseEntity<HashMap<?, ?>> undoComment(@PathVariable Integer issue_id, @PathVariable Integer comment_id) {
        try{
            if(referUser.getCommentUndo().get(issue_id).get(comment_id).isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            if(referUser.getCommentUndo().containsKey(issue_id) && !referUser.getCommentUndo().get(issue_id).get(comment_id).isEmpty()) {
                Comment undoComment=referUser.getCommentUndo().get(issue_id).get(comment_id).pop();
                if(!referUser.getCommentRedo().containsKey(issue_id)) {
                    Stack<Comment> commentStack=new Stack<>();
                    commentStack.push(undoComment);
                    HashMap<Integer,Stack<Comment>> commentMap=new HashMap<>();
                    commentMap.put(comment_id, commentStack);
                    referUser.getCommentRedo().put(issue_id, commentMap);
                }
                else if(!referUser.getCommentRedo().get(issue_id).containsKey(comment_id)) {
                    Stack<Comment> commentStack=new Stack<>();
                    commentStack.push(undoComment);
                    referUser.getCommentRedo().get(issue_id).put(comment_id, commentStack);
                }
                else if(referUser.getCommentRedo().get(issue_id).containsKey(comment_id)) {
                    referUser.getCommentRedo().get(issue_id).get(comment_id).push(undoComment);
                }
                updateTheComment(issue_id, comment_id, referUser.getCommentUndo().get(issue_id).get(comment_id).peek());
                Comment comment=commentService.findCommentById(comment_id);
                HashMap<String,Object> map=new HashMap<>();
                map.put("issue_id", issue_id);
                map.put("comment_id", comment_id);
                map.put("comment", comment);
                return ResponseEntity.ok(map);
            }
        }
        catch(EmptyStackException | NullPointerException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    @GetMapping("/{issue_id}/{comment_id}/comment/redo")
    public ResponseEntity<HashMap<?,?>> redoComment(@PathVariable Integer issue_id, @PathVariable Integer comment_id) {
        if(referUser.getCommentRedo().get(issue_id).get(comment_id).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if(referUser.getCommentRedo().containsKey(issue_id) && referUser.getCommentRedo().get(issue_id).containsKey(comment_id) && !referUser.getCommentRedo().get(issue_id).get(comment_id).isEmpty()) {
            Comment redoComment=referUser.getCommentRedo().get(issue_id).get(comment_id).pop();
            referUser.getCommentUndo().get(issue_id).get(comment_id).push(redoComment);
            updateTheComment(issue_id, comment_id, redoComment);
            Comment comment=commentService.findCommentById(comment_id);
            HashMap<String,Object> map=new HashMap<>();
            map.put("issue_id", issue_id);
            map.put("comment_id", comment_id);
            map.put("comment", comment);
            return ResponseEntity.ok(map);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
