package com.usr_server.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @CrossOrigin(origins = "http://localhost:81")
    @GetMapping("/api/test")
    public String testCors() {
        return "CORS 設置成功!";
    }
}
