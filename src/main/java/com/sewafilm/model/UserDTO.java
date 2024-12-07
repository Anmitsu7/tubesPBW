package com.sewafilm.model;

import lombok.Data;

@Data
public class UserDTO {
    private String username;
    private String password;
    private String email;
    private User.UserRole role;
}