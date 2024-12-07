package com.sewafilm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sewafilm.model.User;
import com.sewafilm.service.UserService;

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

    @PostMapping("/signup")
    public String signupUser(@ModelAttribute("user") User user, Model model) {
        try {
            // Validasi input
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                model.addAttribute("error", "Username tidak boleh kosong");
                return "signup";
            }
            
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                model.addAttribute("error", "Password tidak boleh kosong");
                return "signup";
            }

            // Cek apakah username sudah ada
            if (userService.isUsernameExists(user.getUsername())) {
                model.addAttribute("error", "Username sudah digunakan");
                return "signup";
            }

            // Set role default
            user.setRole(User.UserRole.PELANGGAN);

            // Simpan user
            User savedUser = userService.saveUser(user);
            
            // Redirect ke login page dengan pesan sukses
            return "redirect:/login?registration=success";
            
        } catch (Exception e) {
            logger.error("Error during registration: {}", e.getMessage());
            model.addAttribute("error", "Terjadi kesalahan saat registrasi");
            return "signup";
        }
    }

    // Tambahan method untuk handling registration success message
    @GetMapping("/login")
    public String login(@RequestParam(required = false) String registration, Model model) {
        if (registration != null && registration.equals("success")) {
            model.addAttribute("message", "Registrasi berhasil! Silakan login.");
        }
        return "login";
    }
}