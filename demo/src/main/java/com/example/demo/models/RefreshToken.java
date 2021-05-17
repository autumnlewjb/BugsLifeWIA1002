package com.example.demo.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class RefreshToken {

    @Id
    private Integer id;

    @Column(nullable = false)
    private String token;

    public RefreshToken(Integer id, String token) {
        this.id = id;
        this.token = token;
    }

    public RefreshToken() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
