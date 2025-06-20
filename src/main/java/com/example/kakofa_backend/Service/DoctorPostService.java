package com.example.kakofa_backend.Service;

import com.example.kakofa_backend.Model.DoctorPost;
import com.example.kakofa_backend.Repository.DoctorPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorPostService {

    @Autowired
    private DoctorPostRepository doctorPostRepository;

    public List<DoctorPost> getAllPostsByDoctorId(Long doctorId) {
        return doctorPostRepository.findByDoctorDoctorId(doctorId);
    }

    public DoctorPost createPost(Long doctorId, DoctorPost post) {
        return doctorPostRepository.save(post);
    }

    public DoctorPost getPostById(Long postId) {
        return doctorPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Paylaşım bulunamadı!"));
    }

    public void deletePost(Long postId) {
        DoctorPost post = getPostById(postId);
        doctorPostRepository.delete(post);
    }
}