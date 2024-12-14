package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
// UserController.java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/signup")
    public String showSignUpForm(Model model) {
        logger.debug("Showing sign up form");
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup") // Changed from /api/users/signup to /signup
    public ResponseEntity<?> signupUser(@RequestBody User user) {
        try {
            // Ensure role is set to PELANGGAN for new user registrations
            user.setRole(User.UserRole.PELANGGAN);

            User savedUser = userService.saveUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (Exception e) {
            logger.error("Error saving user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Terjadi kesalahan saat menyimpan pengguna baru: " + e.getMessage());
        }
    }
}
