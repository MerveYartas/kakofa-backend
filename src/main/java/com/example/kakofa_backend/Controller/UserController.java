package com.example.kakofa_backend.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.kakofa_backend.JwtUtil; // JwtUtil'i dahil ettik
import com.example.kakofa_backend.Model.LoginResponse;
import com.example.kakofa_backend.Model.User;
import com.example.kakofa_backend.Repository.UserRepository;
import com.example.kakofa_backend.Service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil; // JwtUtil'i kullanacağız

    @Autowired
    private UserRepository userRepository; // UserRepository'yi enjekte ettik.

    public UserController(UserService userService, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/doctors")
    public List<User> getDoctors() {
        return userService.getDoctors(); // Doktorları al
    }

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestBody User user) {
        System.out.println("isDoctor Değeri: " + user.getIsDoctor());
        System.out.println("Uzmanlık Alanı: " + user.getBranch());

        // Şifreyi şifrele
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Eğer doktor ise, uzmanlık alanı kontrolü yap
        if (user.getIsDoctor() && (user.getBranch() == null || user.getBranch().isEmpty())) {
            return new ResponseEntity<>("Doktor için uzmanlık alanı girilmesi zorunludur.", HttpStatus.BAD_REQUEST);
        }

        try {
            // Kullanıcıyı kaydet
            User registeredUser = userService.registerUser(user);
            return ResponseEntity.ok(registeredUser); // Başarılı yanıt
        } catch (Exception e) {
            // Hata durumunda, hata mesajını döndür
            return new ResponseEntity<>("Kayıt sırasında bir hata oluştu.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        Optional<User> foundUserOptional = userRepository.findByEmail(user.getEmail());

        if (foundUserOptional.isPresent()
                && passwordEncoder.matches(user.getPassword(), foundUserOptional.get().getPassword())) {

            User foundUser = foundUserOptional.get();

            // Kullanıcı doktor mu kontrol et
            boolean isDoctor = foundUser.getIsDoctor(); // Kullanıcı sınıfında doktor olup olmadığını kontrol ediyoruz

            // JWT Token oluştur
            String jwtToken = jwtUtil.generateToken(foundUser);

            // Doktor ise doktor profilini döndür, değilse normal kullanıcı profilini döndür
            return ResponseEntity.ok(new LoginResponse(jwtToken, foundUser, isDoctor));
        } else {
            return ResponseEntity.status(401).body("Email veya şifre hatalı.");
        }
    }

}