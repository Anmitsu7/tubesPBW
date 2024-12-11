package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank(message = "Username tidak boleh kosong")
    private String username;

    @NotBlank(message = "Password tidak boleh kosong")
    private String password;

    // Default constructor
    public LoginRequest() {
    }

    // Constructor with parameters
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getter dan Setter untuk username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getter dan Setter untuk password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Optional: Override toString() method untuk debugging
    @Override
    public String toString() {
        return "LoginRequest{" +
                "username='" + username + '\'' +
                ", password='[PROTECTED]'" + // Don't expose password in logs
                '}';
    }
}