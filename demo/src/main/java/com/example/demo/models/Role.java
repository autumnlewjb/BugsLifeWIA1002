/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "role")
@JsonIgnoreProperties(allowGetters = true)
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer role_id;
    
    @Column(nullable = false, updatable = true, unique = true)
    private String name;
    
    @ManyToMany(fetch=FetchType.LAZY,cascade={CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}, mappedBy="roles")
    @JsonIgnore
    private List<User> users=new ArrayList<>();
    
    public Role(String name) {
        this.name=name;
    }
    
    public Role(){
        
    }
    
    public Integer getRole_id() {
        return role_id;
    }

    public void setRole_id(Integer role_id) {
        this.role_id = role_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
    
    public void addUser(User user) {
        users.add(user);
    }
    
}
