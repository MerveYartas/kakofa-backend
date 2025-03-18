package com.example.kakofa_backend.Repository;

import com.example.kakofa_backend.Model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // Gönderen ve alıcıya göre geçmiş mesajları al ve zaman sırasına göre sırala
    List<Message> findBySenderAndRecipientOrderByTimestampAsc(String sender, String recipient);
}
