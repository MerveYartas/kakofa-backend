package com.example.kakofa_backend;

import java.util.ArrayList;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.kakofa_backend.Model.User;
import com.example.kakofa_backend.Service.UserService;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Email ile kullanıcıyı bul
        User user = userService.findUserByEmailUser(email);

        if (user != null) {
            return new org.springframework.security.core.userdetails.User(user.getEmail(),
                    user.getPassword(),
                    new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("Kullanıcı bulunamadı: " + email);
        }
    }
}
