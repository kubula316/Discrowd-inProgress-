package com.discrowd.server.controller;

import com.discrowd.server.model.Server;
import com.discrowd.server.model.TextChannel;
import com.discrowd.server.model.UserServerMembership;
import com.discrowd.server.model.dto.response.UserServerResponse;
import com.discrowd.server.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;
import org.w3c.dom.Text;

import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@RestController
@RequestMapping("/server")
public class ServerController {

    private final ServerService serverService;



    // Endpoint do tworzenia serwera
    @PostMapping("/create")
    public Server createServer(@RequestBody Server request,
                                                       @RequestHeader("X-User-Id") Long userId) {
        return serverService.createServer(request.getName(), request.getDescription(), userId);
    }

    // Endpoint do dołączania do serwera
    @PostMapping("/join")
    public UserServerMembership joinServer(@RequestParam Long serverId,
                                             @RequestHeader("X-User-Id") Long userId) {
        return serverService.joinServer(serverId, userId);
    }

    // Endpoint do opuszczania serwera
    @PostMapping("/leave")
    public ResponseEntity<String> leaveServer(@RequestParam Long serverId,
                                              @RequestHeader("X-User-Id") Long userId) {
        serverService.leaveServer(serverId, userId);
        return ResponseEntity.ok("Successfully left server " + serverId);
    }

    // Endpoint do pobierania listy serwerów użytkownika
    @GetMapping("/me")
    public List<UserServerResponse> getUserServers(@RequestHeader("X-User-Id") Long userId) {
        return serverService.getUserServers(userId);
    }

    // Endpoint do pobierania szczegółów serwera
    @GetMapping("/details")
    public Server getServerDetails(@RequestParam Long serverId,
                                                           @RequestHeader("X-User-Id") Long userId) {
        // Na razie tylko zwracamy szczegóły, jeśli istnieje
        return serverService.getServerDetails(serverId, userId);
    }

    // Endpoint do tworzenia kanału tekstowego
    @PostMapping("/channels/text")
    public TextChannel createTextChannel(@RequestParam Long serverId,
                                         @RequestParam String channelName,
                                         @RequestHeader("X-User-Id") Long userId) {
        return serverService.createTextChannel(serverId, channelName, userId);
    }

}
