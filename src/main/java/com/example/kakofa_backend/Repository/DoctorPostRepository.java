package com.example.kakofa_backend.Repository;

import com.example.kakofa_backend.Model.DoctorPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorPostRepository extends JpaRepository<DoctorPost, Long> {
    List<DoctorPost> findByDoctorDoctorId(Long doctorId);
}