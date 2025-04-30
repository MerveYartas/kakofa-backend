package com.example.kakofa_backend.Repository;

import com.example.kakofa_backend.Model.Post;
import com.example.kakofa_backend.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUser(User user); // Kullanıcıya göre paylaşım sorgulaması
}
