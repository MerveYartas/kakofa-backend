package com.example.kakofa_backend;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.example.kakofa_backend.Model.User;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretString; // Bu, JWT'nin secret anahtarının değerini alacak

    private SecretKey secretKey; // Bu, JWT'nin şifreleme anahtarını tutacak

    @PostConstruct
    public void init() {
        try {
            // Ensure that the Base64 string is properly padded
            String base64DecodedSecret = new String(Base64.getDecoder().decode(secretString), StandardCharsets.UTF_8);

            // Now use the decoded bytes to create the secure SecretKey
            this.secretKey = Keys.hmacShaKeyFor(base64DecodedSecret.getBytes(StandardCharsets.UTF_8));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid Base64 encoding for JWT secret", e);
        }
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 saatlik geçerlilik süresi
                .signWith(secretKey)
                .compact();
    }

    // extractUsername metodunu ekliyoruz
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey) // Aynı secret key ile doğrulama yapıyoruz
                .build()
                .parseClaimsJws(token) // JWT token'ı çöz
                .getBody()
                .getSubject(); // Token'in subject'ini (yani e-posta) döndür
    }

    // Token'in geçerliliğini kontrol et
    public boolean isTokenValid(String token, User user) {
        return (user.getEmail().equals(extractUsername(token)) && !isTokenExpired(token));
    }

    // Token'in geçerlilik süresi
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }
}