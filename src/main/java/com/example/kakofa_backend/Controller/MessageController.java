package com.example.kakofa_backend.Controller;

import com.example.kakofa_backend.Model.Message;
import com.example.kakofa_backend.Service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    // Mesaj gönderme
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody Message dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Giriş yapmanız gerekiyor.");
        }

        String currentUserEmail = auth.getName();
        if (!currentUserEmail.equals(dto.getSender())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Gönderici sizinle eşleşmiyor.");
        }

        try {
            Message sentMessage = messageService.sendMessage(dto.getSender(), dto.getRecipient(), dto.getContent());
            return ResponseEntity.ok(sentMessage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Mesaj gönderilemedi: " + e.getMessage());
        }
    }

    // Mesaj geçmişi
    @GetMapping("/history/{senderEmail}/{recipientEmail}")
    public ResponseEntity<?> getMessageHistory(@PathVariable String senderEmail,
            @PathVariable String recipientEmail) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Giriş yapmanız gerekiyor.");
        }

        String currentUserEmail = auth.getName();
        if (!currentUserEmail.equals(senderEmail) && !currentUserEmail.equals(recipientEmail)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Bu mesajları görme yetkiniz yok.");
        }

        try {
            List<Message> messages = messageService.getConversation(senderEmail, recipientEmail);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Mesaj geçmişi alınamadı: " + e.getMessage());
        }

    }

    @GetMapping("/has-new")
    public ResponseEntity<?> hasNewMessages() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(false);
        }

        String currentUserEmail = auth.getName();
        boolean hasNew = messageService.hasUnreadMessages(currentUserEmail);
        return ResponseEntity.ok(hasNew);
    }
}
