package com.discrowd.server.repository;


import com.discrowd.server.model.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {

    List<Message> findByChannelIdOrderByTimestampAsc(String channelId);

    List<Message> findByChannelIdOrderByTimestampDesc(String channelId, Pageable pageable);

    List<Message> findByChannelIdOrderByTimestampAsc(String channelId, Pageable pageable);

    List<Message> findByChannelIdAndTimestampBeforeOrderByTimestampDesc(String channelId, LocalDateTime timestamp, Pageable pageable);
}
