package com.example.kakofa_backend.Repository;

import com.example.kakofa_backend.Model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    // Belirli bir kullanıcıya gelen mesajları getirme
    List<Message> findByRecipient(String recipient);

    // Belirli iki kullanıcı arasındaki mesaj geçmişini getirme (isteğe bağlı)
    List<Message> findBySenderAndRecipientOrRecipientAndSenderOrderByTimestampAsc(
            String sender, String recipient, String recipient2, String sender2);
}