package com.usr_server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.usr_server.service.UserService;

@RestController
@RequestMapping("/api/loginUsrAdmin")
public class UserController {

    @Autowired
    private UserService userService;

    // 用戶登錄 API
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        if (userService.isValidUser(username, password)) {
            return "Login successful!";
        } else {
            return "Invalid username or password.";
        }
    }
}