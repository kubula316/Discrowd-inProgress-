package com.discrowd.server.service;


import com.discrowd.server.model.entity.Message;
import com.discrowd.server.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    @Override
    public Message saveMessage(String content, Long senderId, String channelId, String imageUrl) {
        // Tutaj można dodać walidację, np. sprawdzenie, czy channelId faktycznie istnieje
        Message message = new Message(content, senderId, channelId, imageUrl);
        return messageRepository.save(message);
    }

    @Override
    public List<Message> getLatestMessages(String channelId, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit); // Pobierz pierwszą stronę o danym limicie
        List<Message> messages = messageRepository.findByChannelIdOrderByTimestampDesc(channelId, pageRequest);
        Collections.reverse(messages);
        return messages;
    }

    @Override
    public List<Message> getMessagesBefore(String channelId, LocalDateTime beforeTimestamp, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit);
        List<Message> messages = messageRepository.findByChannelIdAndTimestampBeforeOrderByTimestampDesc(channelId, beforeTimestamp, pageRequest);
        Collections.reverse(messages);
        return messages;
    }

}
