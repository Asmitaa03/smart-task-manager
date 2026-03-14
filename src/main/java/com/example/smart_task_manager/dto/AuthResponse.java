package com.example.smart_task_manager.dto;

/**
 * Response for login: user info (no password) + JWT token.
 */
public class AuthResponse {

    private UserResponse user;
    private String token;

    public AuthResponse() {
    }

    public AuthResponse(UserResponse user, String token) {
        this.user = user;
        this.token = token;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
