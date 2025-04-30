package com.example.kakofa_backend.Controller;

import com.example.kakofa_backend.Model.Message;
import com.example.kakofa_backend.Repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class WebSocketChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MessageRepository messageRepository;

    @MessageMapping("/chat") // /app/chat adresine gelen mesajları işler
    public void handleChatMessage(Message message) {
        message.setTimestamp(LocalDateTime.now());
        messageRepository.save(message);

        // Alıcıya gönder: /topic/messages/{recipient}
        messagingTemplate.convertAndSend("/topic/messages/" + message.getRecipient(), message);
    }
}
