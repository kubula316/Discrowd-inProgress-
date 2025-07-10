package com.discrowd.server.service;




import com.discrowd.server.model.entity.Message;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageService {
    public Message saveMessage(String content, Long senderId, String channelId);
    public List<Message> getLatestMessages(String channelId, int limit);
    public List<Message> getMessagesBefore(String channelId, LocalDateTime beforeTimestamp, int limit);
}
