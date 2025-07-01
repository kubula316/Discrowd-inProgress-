package com.discrowd.chat.repository;

import com.discrowd.chat.model.Message;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    /**
     * Znajduje wiadomości dla danego ID kanału, posortowane rosnąco po znaczniku czasu. Używane do pobierania historycznych wiadomości.
     */
    List<Message> findByChannelIdOrderByTimestampAsc(Long channelId);

    /**
     * Znajduje najnowsze wiadomości dla danego ID kanału, posortowane malejąco po znaczniku czasu,
     * z ograniczoną liczbą wyników (paginacja).
     * Używane do pobierania początkowej partii wiadomości po wejściu na kanał.
     */
    List<Message> findByChannelIdOrderByTimestampDesc(Long channelId, Pageable pageable);

    /**
     * Znajduje wiadomości dla danego ID kanału, wysłane przed określonym znacznikiem czasu,
     * posortowane malejąco po znaczniku czasu, z ograniczoną liczbą wyników.
     * Używane do implementacji "infinite scroll" - wczytywania starszych wiadomości.
     */
    List<Message> findByChannelIdAndTimestampBeforeOrderByTimestampDesc(Long channelId, LocalDateTime timestamp, Pageable pageable);
}
