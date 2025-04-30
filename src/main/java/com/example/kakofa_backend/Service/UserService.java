package com.example.kakofa_backend.Service;

import org.springframework.stereotype.Service;

import com.example.kakofa_backend.Model.User;
import com.example.kakofa_backend.Repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(User user) {
        return userRepository.save(user);
    }

    public User findUserByEmailUser(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser.orElse(null);
    }

    public List<User> getDoctors() {
        return userRepository.findByIsDoctorTrue(); // Doktorları getir
    }

    // E-posta adresine göre kullanıcıyı sorgulama
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}