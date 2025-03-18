package com.example.kakofa_backend.Controller;

import com.example.kakofa_backend.Model.Message;
import com.example.kakofa_backend.Service.MessageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // Geçmiş mesajları al
    @GetMapping("/{sender}/{recipient}")
    public List<Message> getMessageHistory(@PathVariable String sender, @PathVariable String recipient) {
        return messageService.getMessageHistory(sender, recipient);
    }

    // Yeni mesaj gönder
    @PostMapping("/send")
    public Message sendMessage(@RequestBody Message message) {
        return messageService.sendMessage(message);
    }
}
