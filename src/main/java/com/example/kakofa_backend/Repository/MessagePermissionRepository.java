package com.example.kakofa_backend.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kakofa_backend.Model.MessagePermission;

public interface MessagePermissionRepository extends JpaRepository<MessagePermission, Long> {
    Optional<MessagePermission> findByUserAndDoctor(String user, String doctor);
}
