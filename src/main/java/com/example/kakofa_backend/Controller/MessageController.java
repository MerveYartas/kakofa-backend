package com.example.kakofa_backend.Controller;

import com.example.kakofa_backend.Model.Message;
import com.example.kakofa_backend.Repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    // Yeni bir mesaj oluşturma (REST API üzerinden gönderme)
    @PostMapping("/send")
    public ResponseEntity<Message> sendMessage(@RequestBody Message message) {
        message.setTimestamp(LocalDateTime.now());
        Message savedMessage = messageRepository.save(message);
        // WebSocket üzerinden ilgili alıcılara da mesajı gönderme mantığı burada
        // olabilir.
        // Örneğin, TextMessageHandler'a bir servis aracılığıyla erişip mesajı
        // yayınlayabilirsiniz.
        return new ResponseEntity<>(savedMessage, HttpStatus.CREATED);
    }

    // Belirli bir kullanıcıya gelen mesajları getirme
    @GetMapping("/inbox/{recipient}")
    public ResponseEntity<List<Message>> getMessagesForRecipient(@PathVariable String recipient) {
        List<Message> messages = messageRepository.findByRecipient(recipient);
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    // Belirli iki kullanıcı arasındaki mesaj geçmişini getirme
    @GetMapping("/history/{user1}/{user2}")
    public ResponseEntity<List<Message>> getConversationHistory(
            @PathVariable String user1,
            @PathVariable String user2) {
        // Özel bir sorgu ile iki kullanıcı arasındaki mesajları getirebilirsiniz.
        // Bu sorguyu MessageRepository'e eklemeniz gerekebilir.
        // Örneğin:
        // findBySenderAndRecipientOrRecipientAndSenderOrderByTimestampAsc(user1, user2,
        // user1, user2);
        // Şimdilik basit bir örnek olarak tüm mesajları dönüyoruz.
        List<Message> allMessages = messageRepository.findAll();
        return new ResponseEntity<>(allMessages, HttpStatus.OK);
    }

    // Tüm mesajları getirme (sadece geliştirme/test amaçlı olabilir)
    @GetMapping("/all")
    public ResponseEntity<List<Message>> getAllMessages() {
        List<Message> messages = messageRepository.findAll();
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    // Mesajı silme (gerekirse)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        messageRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}