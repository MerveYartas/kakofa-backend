
package com.example.kakofa_backend.Controller;

import com.example.kakofa_backend.JwtUtil;
import com.example.kakofa_backend.Model.Post;
import com.example.kakofa_backend.Model.User;
import com.example.kakofa_backend.Repository.PostRepository;
import com.example.kakofa_backend.Repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/create")
    public String createPost(@RequestHeader("Authorization") String token, @RequestParam String content) {
        // Token'dan kullanıcıyı çıkartıyoruz
        String email = jwtUtil.extractUsername(token);

        // E-posta ile kullanıcıyı buluyoruz
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // Paylaşımı veritabanına kaydediyoruz
        Post post = new Post(content, user);
        postRepository.save(post);

        return "Post successfully created!";
    }

    @GetMapping("/user/{email}")
    public List<Post> getUserPosts(@PathVariable String email) {
        // E-posta ile kullanıcıyı buluyoruz
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // Kullanıcının paylaşımlarını döndürüyoruz
        return postRepository.findByUser(user);
    }
}
