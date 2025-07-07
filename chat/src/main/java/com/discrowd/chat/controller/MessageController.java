package com.discrowd.chat.controller;


import com.discrowd.chat.model.Message;
import com.discrowd.chat.model.dto.MessageResponse;
import com.discrowd.chat.model.dto.MessageRequest;
import com.discrowd.chat.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/send")
    public ResponseEntity<MessageResponse> sendMessage(@RequestBody MessageRequest request,
                                                       @RequestHeader("X-User-Id") Long userId,
                                                       @RequestHeader("X-User-Username") String username) {
        // TODO: Docelowo: autoryzacja - czy userId jest członkiem serwera/kanału.
        // To wymagałoby zapytania do Guild Service.
        Message savedMessage = messageService.saveMessage(request.content, userId, username, request.channelId);
        return new ResponseEntity<>(new MessageResponse(savedMessage), HttpStatus.CREATED);
    }

    @GetMapping("/load/latest")
    public ResponseEntity<List<MessageResponse>> getLatestChannelMessages(@RequestParam Long channelId,
                                                                          @RequestParam(defaultValue = "20") int limit) {
        // TODO: Docelowo: autoryzacja - czy użytkownik ma dostęp do tego kanału.
        List<Message> messages = messageService.getLatestMessages(channelId, limit);
        List<MessageResponse> responses = messages.stream()
                .map(MessageResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Endpoint do pobierania starszych wiadomości dla danego kanału (infinite scroll).
     * Pobiera wiadomości wysłane przed określonym znacznikiem czasu.
     */

    @GetMapping("/load/before")
    public ResponseEntity<List<MessageResponse>> getMessagesBefore(
            @RequestParam Long channelId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime timestamp, // Czas, przed którym szukać
            @RequestParam(defaultValue = "50") int limit) {

        // TODO: Docelowo: autoryzacja - czy użytkownik ma dostęp do tego kanału.

        List<Message> messages = messageService.getMessagesBefore(channelId, timestamp, limit);
        List<MessageResponse> responses = messages.stream()
                .map(MessageResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

}
