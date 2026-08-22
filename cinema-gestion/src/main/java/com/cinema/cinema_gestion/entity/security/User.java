package com.cinema.cinema_gestion.entity.security;

import com.cinema.cinema_gestion.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class User extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String role = "USER";

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
