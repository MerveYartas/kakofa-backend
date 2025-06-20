package com.example.kakofa_backend.Controller;

import com.example.kakofa_backend.JwtUtil;
import com.example.kakofa_backend.Model.Doctor;
import com.example.kakofa_backend.Model.User;
import com.example.kakofa_backend.Repository.DoctorRepository;
import com.example.kakofa_backend.Repository.UserRepository;
//import com.example.kakofa_backend.Service.DoctorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "http://localhost:3000")
public class DoctorController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    // @Autowired
    // private DoctorService doctorService;

    private String extractEmailFromToken(String token) {
        try {
            String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
            return jwtUtil.extractUsername(jwt);
        } catch (Exception e) {
            throw new RuntimeException("Geçersiz token: " + e.getMessage());
        }
    }

    // Doktor bilgilerini getir veya ilk kez oluştur
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentDoctor(@RequestHeader("Authorization") String token) {
        try {
            String email = extractEmailFromToken(token);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + email));

            Optional<Doctor> existing = doctorRepository.findByUser(user);
            Doctor doctor = existing.orElseGet(() -> {
                Doctor newDoctor = new Doctor();
                newDoctor.setUser(user);
                newDoctor.setFirstName(user.getFirstname() != null ? user.getFirstname() : "Doktor");
                newDoctor.setLastName(user.getLastname() != null ? user.getLastname() : "Soyad");
                newDoctor.setCreatedAt(LocalDateTime.now());
                return doctorRepository.save(newDoctor);
            });

            return ResponseEntity.ok(doctor);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Hata: " + e.getMessage());
        }
    }

    // Doktor bilgilerini güncelle
    @PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateDoctor(
            @RequestHeader("Authorization") String token,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "specialization", required = false) String specialization,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "profilePhoto", required = false) MultipartFile profilePhoto) {

        try {
            String email = extractEmailFromToken(token);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + email));

            Doctor doctor = doctorRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Doktor bulunamadı!"));

            doctor.setFirstName(firstName);
            doctor.setLastName(lastName);
            doctor.setTitle(title);
            doctor.setSpecialization(specialization);
            doctor.setDescription(description);

            if (profilePhoto != null && !profilePhoto.isEmpty()) {
                String fileName = UUID.randomUUID() + "_" + profilePhoto.getOriginalFilename();
                String uploadDir = "Uploads/";
                File uploadDirFile = new File(uploadDir);
                if (!uploadDirFile.exists())
                    uploadDirFile.mkdirs();

                File destination = new File(uploadDir + fileName);
                profilePhoto.transferTo(destination);
                doctor.setProfilePhotoUrl("/Uploads/" + fileName);
            }

            Doctor updated = doctorRepository.save(doctor);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Güncelleme hatası: " + e.getMessage());
        }
    }
}
