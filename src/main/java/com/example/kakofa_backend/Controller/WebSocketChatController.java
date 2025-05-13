package com.example.kakofa_backend.Controller;

import com.example.kakofa_backend.Model.Message;
import com.example.kakofa_backend.Model.MessagePermission;
import com.example.kakofa_backend.Repository.MessagePermissionRepository;
import com.example.kakofa_backend.Repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class WebSocketChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessagePermissionRepository messagePermissionRepository;

    @MessageMapping("/chat") // /app/chat adresine gelen mesajları işler
    public void handleChatMessage(Message message) {
        // Mesaj izni kontrolü
        Optional<MessagePermission> permission = messagePermissionRepository.findByUserAndDoctor(message.getSender(),
                message.getRecipient());

        if (permission.isPresent() && permission.get().isApproved()) {
            message.setTimestamp(LocalDateTime.now());
            messageRepository.save(message);

            // Alıcıya gönder: /topic/messages/{recipient}
            messagingTemplate.convertAndSend("/topic/messages/" + message.getRecipient(), message);
        } else {
            // İzin verilmediği durum için uygun işlem yapılabilir
            throw new IllegalStateException("Mesaj gönderme izniniz yok.");
        }
    }
}
