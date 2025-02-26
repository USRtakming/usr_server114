package com.usr_server.service;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final String CORRECT_USERNAME = "2024UsrAdmin";
    private static final String CORRECT_PASSWORD = "P@55w0rd#7Xz";

    // 驗證用戶名和密碼
    public boolean isValidUser(String username, String password) {
        return CORRECT_USERNAME.equals(username) && CORRECT_PASSWORD.equals(password);
    }
}
