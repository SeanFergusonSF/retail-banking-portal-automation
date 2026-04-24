package com.mockbank.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BankController {

    // Home page
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("bankName", "NorthBank");
        return "home";
    }

    // Products page
    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("pageTitle", "Our Products");
        return "products";
    }

    // Login page - GET
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // Login page - POST (handles form submission)
    @PostMapping("/login")
    public String loginSubmit(
            @RequestParam String username,
            @RequestParam String password,
            Model model) {

        // Simple mock authentication - no real security
        if (username.equals("standard_user") && password.equals("password123")) {
            return "redirect:/dashboard";
        } else if (username.equals("locked_user")) {
            model.addAttribute("errorMessage", "Your account has been locked.");
            return "login";
        } else {
            model.addAttribute("errorMessage", "Invalid username or password.");
            return "login";
        }
    }

    // Dashboard page
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("customerName", "John Smith");
        model.addAttribute("segment", "Premium");
        return "dashboard";
    }
}