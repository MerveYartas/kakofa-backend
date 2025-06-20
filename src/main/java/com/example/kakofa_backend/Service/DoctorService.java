package com.example.kakofa_backend.Service;

import com.example.kakofa_backend.Model.Doctor;
import com.example.kakofa_backend.Model.User;
import com.example.kakofa_backend.Repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    public Doctor findByUser(User user) {
        return doctorRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Doktor bulunamadı!"));
    }

    public Optional<Doctor> findById(Long doctorId) {
        return doctorRepository.findById(doctorId);
    }

    public Doctor save(Doctor doctor) {
        return doctorRepository.save(doctor);
    }
}