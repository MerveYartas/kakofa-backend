package com.example.kakofa_backend.Repository;

import com.example.kakofa_backend.Model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findBySenderAndRecipientOrRecipientAndSenderOrderByTimestampAsc(
            String sender1, String recipient1, String recipient2, String sender2);
}
