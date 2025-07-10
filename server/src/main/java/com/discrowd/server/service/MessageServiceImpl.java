package com.discrowd.server.service;


import com.discrowd.server.model.entity.Message;
import com.discrowd.server.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    @Override
    public Message saveMessage(String content, Long senderId, String channelId) {
        // Tutaj można dodać walidację, np. sprawdzenie, czy channelId faktycznie istnieje
        // (poprzez wywołanie Feign do Guild Service, jeśli masz już to skonfigurowane).
        // Na razie zakładamy, że channelId jest poprawne.
        Message message = new Message(content, senderId, channelId);
        return messageRepository.save(message);
    }

    @Override
    public List<Message> getLatestMessages(String channelId, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit); // Pobierz pierwszą stronę o danym limicie
        return messageRepository.findByChannelIdOrderByTimestampAsc(channelId, pageRequest);
    }

    @Override
    public List<Message> getMessagesBefore(String channelId, LocalDateTime beforeTimestamp, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit);
        return messageRepository.findByChannelIdAndTimestampBeforeOrderByTimestampDesc(channelId, beforeTimestamp, pageRequest);
    }

}
