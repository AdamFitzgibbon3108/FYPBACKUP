package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    // Display the login page
    @GetMapping("/login")
    public String login() {
        return "login"; // This renders login.html
    }
}


