package com.example.kakofa_backend.Model;

public class LoginResponse {

    private String token;
    private User user;
    private boolean isDoctor; // Doktor olup olmadığını belirten alan

    // Constructor
    public LoginResponse(String token, User user, boolean isDoctor) {
        this.token = token;
        this.user = user;
        this.isDoctor = isDoctor; // Doktor bilgisini de ekliyoruz
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isDoctor() {
        return isDoctor;
    }

    public void setDoctor(boolean isDoctor) {
        this.isDoctor = isDoctor;
    }
}
