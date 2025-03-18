package com.example.kakofa_backend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class TestConfig {

    @Value("${jwt.secret:NOT_FOUND}") // Eğer bulunamazsa "NOT_FOUND" yazdırır
    private String jwtSecret;

    @PostConstruct
    public void init() {
        System.out.println("JWT Secret from properties: " + jwtSecret);
    }
}
