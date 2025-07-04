package com.example.kakofa_backend.Service;

import com.example.kakofa_backend.Model.Message;
import com.example.kakofa_backend.Repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public Message sendMessage(String sender, String recipient, String content) {
        Message message = new Message();
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        return messageRepository.save(message);
    }

    public List<Message> getConversation(String sender, String recipient) {
        return messageRepository.findBySenderAndRecipient(sender, recipient);
    }

    public boolean hasUnreadMessages(String userEmail) {
        return messageRepository.existsByRecipientAndIsReadFalse(userEmail);
    }

}
