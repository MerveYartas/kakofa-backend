package com.example.kakofa_backend.Controller;

import com.example.kakofa_backend.JwtUtil;
import com.example.kakofa_backend.Model.Doctor;
import com.example.kakofa_backend.Model.DoctorPost;
import com.example.kakofa_backend.Model.User;
import com.example.kakofa_backend.Repository.DoctorRepository;
import com.example.kakofa_backend.Repository.UserRepository;
import com.example.kakofa_backend.Service.DoctorPostService;

import io.jsonwebtoken.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/doctor-posts")
@CrossOrigin(origins = "*")
public class DoctorPostController {

    @Autowired
    private DoctorPostService doctorPostService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private String extractUsernameFromToken(String token) {
        try {
            String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
            String email = jwtUtil.extractUsername(jwt);
            System.out.println("Çıkarılan e-posta: " + email);
            return email;
        } catch (Exception e) {
            System.out.println("Token doğrulama hatası: " + e.getMessage());
            throw new RuntimeException("Geçersiz token: " + e.getMessage());
        }
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<?> getAllPostsByDoctor(
            @PathVariable Long doctorId,
            @RequestHeader("Authorization") String token) {
        try {
            String email = extractUsernameFromToken(token);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + email));
            Doctor doctor = doctorRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Doktor profili bulunamadı: " + email));
            if (!doctor.getDoctorId().equals(doctorId)) {
                throw new RuntimeException("Geçersiz doktor ID!");
            }

            List<DoctorPost> posts = doctorPostService.getAllPostsByDoctorId(doctorId);
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            System.out.println("Hata: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Hata: " + e.getMessage());
        }
    }

    @PostMapping(value = "/doctor/{doctorId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPost(
            @PathVariable Long doctorId,
            @RequestHeader("Authorization") String token,
            @RequestParam("content") String content,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "media", required = false) MultipartFile media) {

        try {

            // Token'dan kullanıcıyı al
            String email = extractUsernameFromToken(token);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

            // Doktoru bul veya oluştur
            Doctor doctor = doctorRepository.findByUser(user).orElseGet(() -> createDoctor(user));

            // Doktorun ID'sini doğrula
            if (!doctor.getDoctorId().equals(doctorId)) {
                throw new RuntimeException("Geçersiz doktor ID!");
            }

            // İçerik boş olamaz
            if (content == null || content.trim().isEmpty()) {
                throw new RuntimeException("İçerik boş olamaz!");
            }

            // Post oluştur
            DoctorPost post = new DoctorPost();
            post.setContent(content);
            post.setTitle(title);
            post.setDoctor(doctor);

            // Dosya yükle
            if (media != null && !media.isEmpty()) {
                String imageUrl = saveMedia(media);
                post.setImageUrl(imageUrl);
            }

            // Paylaşımı kaydet
            DoctorPost savedPost = doctorPostService.createPost(doctorId, post);
            return ResponseEntity.ok(savedPost);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Hata: " + e.getMessage());
        }
    }

    // Doktor oluşturma fonksiyonu
    private Doctor createDoctor(User user) {
        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setFirstName(user.getFirstname() != null ? user.getFirstname() : "Doktor");
        doctor.setLastName(user.getLastname() != null ? user.getLastname() : "Soyad");
        return doctorRepository.save(doctor);
    }

    // Media dosyasını kaydetme fonksiyonu
    private String saveMedia(MultipartFile media) throws IOException {
        String fileName = UUID.randomUUID() + "_" + media.getOriginalFilename();
        String uploadDir = "Uploads/";

        // Dizin varsa, oluştur
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }

        File file = new File(uploadDir + fileName);
        try {
            media.transferTo(file);
        } catch (IllegalStateException | java.io.IOException e) {
            e.printStackTrace();
        }
        return "/Uploads/" + fileName;
    }

    @GetMapping("/{postId}")
    public ResponseEntity<?> getPostById(
            @PathVariable Long postId,
            @RequestHeader("Authorization") String token) {
        try {
            String email = extractUsernameFromToken(token);
            userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + email));
            DoctorPost post = doctorPostService.getPostById(postId);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            System.out.println("Hata: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Hata: " + e.getMessage());
        }
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(
            @PathVariable Long postId,
            @RequestHeader("Authorization") String token) {
        try {
            String email = extractUsernameFromToken(token);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + email));
            DoctorPost post = doctorPostService.getPostById(postId);
            Doctor doctor = doctorRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Doktor profili bulunamadı: " + email));
            if (!post.getDoctor().getDoctorId().equals(doctor.getDoctorId())) {
                throw new RuntimeException("Bu paylaşımı silme yetkiniz yok!");
            }

            doctorPostService.deletePost(postId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            System.out.println("Hata: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Hata: " + e.getMessage());
        }
    }
}