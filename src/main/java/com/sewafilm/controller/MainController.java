package com.sewafilm.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class MainController {

    @GetMapping({"/"})
    public String homepage(Model model) {
        return "index";  // 
    }

    @GetMapping("/about")
    public String about(Model model) {
        return "about";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(
        @RequestParam String email, 
        @RequestParam String password, 
        Model model
    ) {
        return "redirect:/homepageafterlogin";
    }
}