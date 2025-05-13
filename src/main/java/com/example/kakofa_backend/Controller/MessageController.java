package com.example.kakofa_backend.Controller;

import com.example.kakofa_backend.Model.Message;
import com.example.kakofa_backend.Service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    // ✅ Mesaj gönderme (email bazlı)
    @PostMapping("/send")
    public Message sendMessage(@RequestBody Message dto) {
        return messageService.sendMessage(dto.getSender(), dto.getRecipient(), dto.getContent());
    }

    // ✅ İki email adresi arasındaki mesajları getir
    @GetMapping("/history/{senderEmail}/{recipientEmail}")
    public List<Message> getMessageHistory(@PathVariable String senderEmail,
            @PathVariable String recipientEmail) {
        return messageService.getConversation(senderEmail, recipientEmail);
    }
}
