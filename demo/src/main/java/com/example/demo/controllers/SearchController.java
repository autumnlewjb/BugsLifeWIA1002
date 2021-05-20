package com.example.demo.controllers;

import com.example.demo.models.User;
import com.example.demo.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SearchController {

    @Autowired
    SearchService searchService;

    @GetMapping("/search")
    public ResponseEntity<Page<User>> searchPersons(Pageable pageable, @RequestParam("query") String query) {
        Page<User> result = searchService.search(pageable, query);
        return ResponseEntity.ok(result);
    }
}
