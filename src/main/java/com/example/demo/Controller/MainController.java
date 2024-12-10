package com.example.demo.Controller;

import com.example.demo.User.User;
import com.example.demo.User.UserRepository;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.DTO.LoginRequest;


@Controller
public class MainController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping({ "/", "/home" })
    public String homepage(Model model) {
        System.out.println("Homepage method called");
        return "homepage";
    }

    @GetMapping("/about")
    public String about(Model model) {
        return "about";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/explore")
    public String explore(Model model) {
        return "explore";
    }
    
    @GetMapping("/explore2")
    public String explore2(Model model) {
        return "explore2";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        return "profile";
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        
        // Cari user berdasarkan username
        User user = userRepository.findByUsername(username);
        
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            // Login berhasil
            return ResponseEntity.ok("Login successful");
        } else {
            // Login gagal
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Username atau password salah");
        }
    }

    @GetMapping("/homepageafterlogin")
    public String home(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("username", principal.getName());
        }
        return "HomepageAfterLogin";
    }
    
    @GetMapping("/user-data")
    @ResponseBody
    public Map<String, String> getUserData(Principal principal) {
        Map<String, String> userData = new HashMap<>();
        if (principal != null) {
            userData.put("username", principal.getName());
        }
        return userData;
    }
    
}