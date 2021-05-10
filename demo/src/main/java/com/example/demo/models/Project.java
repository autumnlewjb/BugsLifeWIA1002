// package com.example.demo.models;

// import java.util.Date;

// import javax.persistence.Column;
// import javax.persistence.Entity;
// import javax.persistence.GeneratedValue;
// import javax.persistence.GenerationType;
// import javax.persistence.Id;
// import javax.persistence.JoinColumn;
// import javax.persistence.ManyToOne;
// import javax.persistence.Table;

// @Entity
// @Table(name = "project")
// public class Project {
    
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Integer id;

//     @Column(name = "name")
//     private String name;

//     @Column(name = "date_created")
//     private Date date;

//     @ManyToOne
//     @JoinColumn(name = "issue_id")
//     private Issue issue;

//     public Integer getId() {
//         return id;
//     }

//     public void setId(Integer id) {
//         this.id = id;
//     }

//     public String getName() {
//         return name;
//     }

//     public void setName(String name) {
//         this.name = name;
//     }

//     public Date getDate() {
//         return date;
//     }

//     public void setDate(Date date) {
//         this.date = date;
//     }

//     public Issue getIssue() {
//         return issue;
//     }

//     public void setIssue(Issue issue) {
//         this.issue = issue;
//     }

// }
