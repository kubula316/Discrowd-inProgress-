package com.discrowd.server.service;

import com.discrowd.server.model.Server;
import com.discrowd.server.model.TextChannel;
import com.discrowd.server.model.UserServerMembership;
import com.discrowd.server.model.VoiceChannel;
import com.discrowd.server.model.dto.response.UserServerResponse;
import com.discrowd.server.repository.ServerRepository;
import com.discrowd.server.repository.TextChannelRepository;
import com.discrowd.server.repository.UserServerMembershipRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServerServiceImpl implements ServerService{

    private final ServerRepository serverRepository;
    private final UserServerMembershipRepository membershipRepository;
    private final TextChannelRepository textChannelRepository;


    @Override
    @Transactional
    public Server createServer(String name, String description, Long ownerId) {
        Server server = Server.builder()
                .name(name)
                .description(description)
                .iconUrl("https://developers.elementor.com/docs/assets/img/elementor-placeholder-image.png")
                .ownerId(ownerId)
                .textChannels(new ArrayList<TextChannel>())
                .voiceChannels(new ArrayList<VoiceChannel>())
                .build();
        server = serverRepository.save(server);

        UserServerMembership ownerMembership = UserServerMembership.builder()
                .userId(ownerId)
                .server(server)
                .role("OWNER")
                .build();
        membershipRepository.save(ownerMembership);

        TextChannel generalChannel = TextChannel.builder()
                .name("general")
                .server(server)
                .build();
        server.addTextChannel(generalChannel);
        textChannelRepository.save(generalChannel);

        return server;
    }

    @Override
    @Transactional
    public UserServerMembership joinServer(Long serverId, Long userId) {
        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new RuntimeException("Server not found"));

        if (membershipRepository.findByUserIdAndServerId(userId, serverId).isPresent()) {
            throw new RuntimeException("User is already a member of this server");
        }

        UserServerMembership membership = UserServerMembership.builder()
                .userId(userId)
                .server(server)
                .role("MEMBER")
                .build();
        return membershipRepository.save(membership);
    }

    @Override
    @Transactional
    public void leaveServer(Long serverId, Long userId) {
        Optional<UserServerMembership> membershipOpt = membershipRepository.findByUserIdAndServerId(userId, serverId);
        if (membershipOpt.isEmpty()) {
            throw new RuntimeException("User is not a member of this server");
        }

        // Dodaj logikę, jeśli właściciel opuszcza serwer
        // Np. transfer własności, usunięcie serwera jeśli to jedyny członek
        // Na razie proste usunięcie członkostwa
        membershipRepository.delete(membershipOpt.get());
    }

    @Override
    @Transactional()
    public List<UserServerResponse> getUserServers(Long userId) {
        List<UserServerMembership> memberships = membershipRepository.findByUserId(userId);
        return memberships.stream()
                .map(membership -> {
                    Server server = membership.getServer();
                    UserServerResponse response = new UserServerResponse();
                    response.setId(server.getId());
                    response.setName(server.getName());
                    response.setIconUrl(server.getIconUrl());
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Server getServerDetails(Long serverId, Long userId) {
        if (membershipRepository.findByUserIdAndServerId(userId, serverId).isPresent()) {
            Server server = serverRepository.findById(serverId).orElseThrow(() -> new RuntimeException("Server not found"));
            // przemapować na DTO potem
            return Server.builder()
                    .id(server.getId())
                    .name(server.getName())
                    .description(server.getDescription())
                    .iconUrl(server.getIconUrl())
                    .ownerId(server.getOwnerId())
                    .textChannels(server.getTextChannels().stream().map(tc -> TextChannel.builder()
                            .id(tc.getId())
                            .name(tc.getName())
                            .server(tc.getServer())
                            .build()).collect(Collectors.toList()))
                    .voiceChannels(server.getVoiceChannels()) // Assuming VoiceChannel is handled similarly
                    .build();
        }
        throw new RuntimeException("User is not a member of this server");
    }

    @Override
    @Transactional
    public TextChannel createTextChannel(Long serverId, String channelName, Long requestingUserId) {
        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new RuntimeException("Server not found"));

        // TODO: Na tym etapie pomijamy autoryzację, ale tutaj docelowo sprawdzisz,
        // czy requestingUserId ma uprawnienia do tworzenia kanałów na tym serwerze.
        // Np. czy jest właścicielem lub administratorem.

        TextChannel channel = TextChannel.builder()
                .name(channelName)
                .server(server)
                .build();
        server.addTextChannel(channel);
        return textChannelRepository.save(channel);
    }


}
