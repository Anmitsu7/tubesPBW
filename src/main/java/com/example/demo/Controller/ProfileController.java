package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.service.UserService;
import com.example.demo.service.BookingService;
import com.example.demo.model.User;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    @GetMapping("/profile")
    public String showProfile(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        
        // Get user data
        model.addAttribute("user", user);
        
        // Get rental statistics
        model.addAttribute("totalRentals", bookingService.getTotalRentals(user));
        model.addAttribute("activeRentals", bookingService.getActiveRentals(user));
        
        // Get recent rentals
        model.addAttribute("recentRentals", bookingService.getRecentRentals(user));
        
        return "profile";
    }
}