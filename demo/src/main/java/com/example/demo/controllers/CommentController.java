package com.example.demo.controllers;

import static com.example.demo.controllers.AuthController.referUser;
import java.util.List;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.models.Comment;
import com.example.demo.models.Issue;
import com.example.demo.services.CommentService;

import com.example.demo.services.IssueService;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CommentController {

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
        return ResponseEntity.ok(commentList);
    }

    @PostMapping("{project_id}/{issue_id}")
    public ResponseEntity<Comment> createComments(@PathVariable Integer issue_id, @RequestBody Comment comment) {
        Issue issue = issueService.findIssuesById(issue_id);
        if (issue == null) {
            throw new ResourceNotFoundException("issue", "id", issue_id);
        }
        referUser.getIssueIdUndo().push(issue_id);
        issue.getComment().add(comment);
        comment.setIssue(issue);
        commentService.createComments(comment);
        referUser.getUndo().push(comment.getComment_id());
        referUser.getIssueIdRedo().clear();
        referUser.getRedo().clear();
        return ResponseEntity.ok(comment);
    }
    
    public void createComment(Integer issue_id, Comment comment) {
        Issue issue = issueService.findIssuesById(issue_id);
        referUser.getIssueIdUndo().push(issue_id);
        issue.getComment().add(comment);
        comment.setIssue(issue);
        commentService.createComments(comment);
        referUser.getUndo().push(comment.getComment_id());
    }

    @PutMapping("{project_id}/{issue_id}/{comment_id}")
    public ResponseEntity<?> updateComment( @PathVariable Integer issue_id, @PathVariable Integer comment_id, @RequestBody Comment updatedComment){
        Comment comment = commentService.findCommentById(comment_id);
        if (comment == null) {
            throw new ResourceNotFoundException("comment", "id", comment_id);
        }
        commentService.updateComment(issue_id, comment, updatedComment);
        return ResponseEntity.ok(HttpStatus.OK);
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
        if(referUser.getUndo().contains(comment_id)) {
            int index=referUser.getUndo().indexOf(comment_id);
            referUser.getUndo().remove(index);
            referUser.getIssueIdUndo().remove(index);
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }
    
    @GetMapping("/undo")
    public ResponseEntity<HashMap<Integer,Comment>> undoComment() {
        if(!referUser.getUndo().isEmpty()) {
            HashMap<Integer,Comment> map=new HashMap<>();
            Integer reference=referUser.getUndo().pop();
            referUser.getIssueIdRedo().push(referUser.getIssueIdUndo().pop());
            Comment comment=commentService.findCommentById(reference);
            deleteComment(referUser.getIssueIdRedo().peek(), reference);
            referUser.getRedo().push(comment);
            Integer issueID=referUser.getIssueIdRedo().peek();
            map.put(issueID, comment);
            return ResponseEntity.ok(map);
        }
        else {
            return null;
        }
    }
    
    @GetMapping("/redo")
    public ResponseEntity<HashMap<Integer,Comment>> redoComment() {
        if(!referUser.getRedo().isEmpty()) {
            HashMap<Integer,Comment> map=new HashMap<>();
            Integer issueID=referUser.getIssueIdRedo().peek();
            Comment comment=referUser.getRedo().pop();
            comment.setComment_id(null);
            comment.setReact(null);
            createComment(referUser.getIssueIdRedo().pop(),comment);
            comment=commentService.findAllComments().get(commentService.findAllComments().size()-1);
            map.put(issueID, comment);
            return ResponseEntity.ok(map);
        }
        else {
            return null;
        }
    }
}
