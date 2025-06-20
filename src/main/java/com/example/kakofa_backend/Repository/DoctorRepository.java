package com.example.kakofa_backend.Repository;

import com.example.kakofa_backend.Model.Doctor;
import com.example.kakofa_backend.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUser(User user);
}