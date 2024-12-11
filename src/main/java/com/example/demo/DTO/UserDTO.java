package com.example.demo.dto;

import com.example.demo.model.User;

import lombok.Data;

@Data
public class UserDTO {
    private String username;
    private String password;
    private String email;
    private User.UserRole role;
}