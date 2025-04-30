package com.example.kakofa_backend.Service;

import com.example.kakofa_backend.Model.Message;
import com.example.kakofa_backend.Repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    // Yeni mesaj gönder
    public Message sendMessage(Message message) {
        message.setTimestamp(LocalDateTime.now()); // Zaman bilgisini ekle
        return messageRepository.save(message); // Mesajı veritabanına kaydet
    }
}
