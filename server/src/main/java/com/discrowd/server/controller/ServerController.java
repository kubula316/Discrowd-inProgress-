package com.discrowd.server.controller;

import com.discrowd.server.model.dto.ChannelCategoryDto;
import com.discrowd.server.model.dto.ServerDetailsResponse;
import com.discrowd.server.model.dto.TextChannelDto;
import com.discrowd.server.model.dto.VoiceChannelDto;
import com.discrowd.server.model.entity.TextChannel;
import com.discrowd.server.model.request.CreateCategoryRequest;
import com.discrowd.server.model.request.CreateTextChannelRequest;
import com.discrowd.server.model.request.CreateVoiceChannelRequest;
import com.discrowd.server.model.response.UserServerResponse;
import com.discrowd.server.model.request.CreateServerRequest;
import com.discrowd.server.model.response.ServerResponse;
import com.discrowd.server.model.response.UserServerMembershipResponse;
import com.discrowd.server.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/server")
public class ServerController {

    private final ServerService serverService;

    // Endpoint do tworzenia serwera
    @PostMapping("/create")
    public ResponseEntity<ServerResponse> createServer(@RequestBody CreateServerRequest request, @RequestHeader("X-User-Id") Long userId) {
        ServerResponse responseDto = serverService.createServer(request.getName(), userId);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    // Endpoint do dołączania do serwera
    @PostMapping("/join")
    public ResponseEntity<UserServerMembershipResponse> joinServer(@RequestParam Long serverId, @RequestHeader("X-User-Id") Long userId) {
        // Serwis zwraca już DTO
        UserServerMembershipResponse response = serverService.joinServer(serverId, userId);
        return ResponseEntity.ok(response);
    }

    // Endpoint do opuszczania serwera
    @PostMapping("/leave")
    public ResponseEntity<String> leaveServer(@RequestParam Long serverId, @RequestHeader("X-User-Id") Long userId) {
        serverService.leaveServer(serverId, userId);
        return ResponseEntity.ok("Successfully left server " + serverId);
    }

    // Endpoint do pobierania listy serwerów użytkownika (bez zmian w zwracanym typie)
    @GetMapping("/me")
    public List<UserServerResponse> getUserServers(@RequestHeader("X-User-Id") Long userId) {
        return serverService.getUserServers(userId);
    }

    // Endpoint do pobierania szczegółów serwera (bez zmian w zwracanym typie)
    @GetMapping("/details")
    public ServerDetailsResponse getServerDetails(@RequestParam Long serverId, @RequestHeader("X-User-Id") Long userId) {
        return serverService.getServerDetails(serverId, userId);
    }

    // Endpoint do tworzenia kanału tekstowego
    @PostMapping("/channels/text")
    public ResponseEntity<TextChannelDto> createTextChannel(@RequestBody CreateTextChannelRequest request, @RequestHeader("X-User-Id") Long userId) {
        TextChannelDto responseDto = serverService.createTextChannel(request.getServerId(), request.getChannelName(), userId, request.getCategoryId());
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    // Endpoint do tworzenia kanału głosowego
    @PostMapping("/channels/voice")
    public ResponseEntity<VoiceChannelDto> createVoiceChannel(@RequestBody CreateVoiceChannelRequest request, @RequestHeader("X-User-Id") Long userId) {
        VoiceChannelDto responseDto = serverService.createVoiceChannel(request.getServerId(), request.getChannelName(), userId, request.getCategoryId());
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    // Endpoint do tworzenia kategorii
    @PostMapping("/categories")
    public ResponseEntity<ChannelCategoryDto> createCategory(@RequestBody CreateCategoryRequest request, @RequestHeader("X-User-Id") Long userId) {
        ChannelCategoryDto responseDto = serverService.createCategory(request.getServerId(), request.getCategoryName(), userId);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

}
