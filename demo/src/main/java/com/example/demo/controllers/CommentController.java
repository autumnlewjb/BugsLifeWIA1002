package com.example.demo.controllers;

import static com.example.demo.controllers.AuthController.referUser;
import java.util.List;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.models.Comment;
import com.example.demo.models.Issue;
import com.example.demo.services.CommentService;

import com.example.demo.services.IssueService;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Stack;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    
    @Transactional
    @GetMapping("/comment/{comment_id}/history")
    public ResponseEntity<?> getHistory(@PathVariable Integer comment_id) {
        Comment comment=commentService.findCommentById(comment_id);
        //if(referUser.getUsername().equals(comment.getUser())) {
            return ResponseEntity.ok(commentService.getHistory(comment_id));
        //}
        //return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    @Transactional
    @GetMapping("/{username}/comment/history")
    public ResponseEntity<?> getOwnHistory(@PathVariable String username) {
        return ResponseEntity.ok(commentService.getOwnHistory(username));
    }
    
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
