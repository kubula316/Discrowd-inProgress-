package com.discrowd.chat.service;


import com.discrowd.chat.model.Message;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageService {
    public Message saveMessage(String content, Long senderId, String senderUsername, Long channelId);
    public List<Message> getLatestMessages(Long channelId, int limit);
    public List<Message> getMessagesBefore(Long channelId, LocalDateTime beforeTimestamp, int limit);
}
