package com.discrowd.server.service;

import com.discrowd.server.model.dto.*;
import com.discrowd.server.model.entity.Server;
import com.discrowd.server.model.entity.TextChannel;
import com.discrowd.server.model.entity.UserServerMembership;
import com.discrowd.server.model.entity.VoiceChannel;
import com.discrowd.server.model.entity.ChannelCategory;
import com.discrowd.server.model.response.ServerResponse;
import com.discrowd.server.model.response.UserServerMembershipResponse;
import com.discrowd.server.model.response.UserServerResponse;
import com.discrowd.server.repository.*;
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
    private final ChannelCategoryRepository channelCategoryRepository;
    private final VoiceChannelRepository voiceChannelRepository;


    //TODO za pomocą feigna dodać dane użytkownika do membershipa nickname/avatarUrl

    @Override
    @Transactional
    public ServerResponse createServer(String name, Long ownerId) {
        Server server = Server.builder()
                .name(name)
                .iconUrl("https://developers.elementor.com/docs/assets/img/elementor-placeholder-image.png")
                .ownerId(ownerId)
                .categories(new ArrayList<>())
                .build();
        server = serverRepository.save(server);

        UserServerMembership ownerMembership = UserServerMembership.builder()
                .userId(ownerId)
                .server(server)
                .role("OWNER")
                .build();
        membershipRepository.save(ownerMembership);

        // Tworzenie 1 kat
        ChannelCategory defaultTextCategory = ChannelCategory.builder()
                .name("Text Channels")
                .server(server)
                .position(0)
                .textChannels(new ArrayList<>())
                .voiceChannels(new ArrayList<>())
                .build();
        server.addCategory(defaultTextCategory);
        defaultTextCategory = channelCategoryRepository.save(defaultTextCategory);

        TextChannel generalChannel = TextChannel.builder()
                .name("general")
                .server(server)
                .category(defaultTextCategory)
                .position(0)
                .build();
        defaultTextCategory.addTextChannel(generalChannel);
        textChannelRepository.save(generalChannel);

        // Tworzenie 2 kat
        ChannelCategory defaultVoiceCategory = ChannelCategory.builder()
                .name("Voice Channels")
                .server(server)
                .position(1)
                .textChannels(new ArrayList<>())
                .voiceChannels(new ArrayList<>())
                .build();
        server.addCategory(defaultVoiceCategory);
        defaultVoiceCategory = channelCategoryRepository.save(defaultVoiceCategory);

        VoiceChannel generalVoiceChannel = VoiceChannel.builder()
                .name("General")
                .server(server)
                .category(defaultVoiceCategory)
                .position(0)
                .build();
        defaultVoiceCategory.addVoiceChannel(generalVoiceChannel);
        voiceChannelRepository.save(generalVoiceChannel);

        return ServerResponse.builder()
                .id(server.getId())
                .name(server.getName())
                .iconUrl(server.getIconUrl())
                .ownerId(server.getOwnerId())
                .build();
    }

    @Override
    @Transactional
    public UserServerMembershipResponse joinServer(Long serverId, Long userId) {
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

        membershipRepository.save(membership);

        return UserServerMembershipResponse.builder()
                .membershipId(membership.getId())
                .userId(membership.getUserId())
                .serverId(membership.getServer().getId())
                .role(membership.getRole())
                .build();
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
    public ServerDetailsResponse getServerDetails(Long serverId, Long userId) {
        if (membershipRepository.findByUserIdAndServerId(userId, serverId).isPresent()) {
            Server server = serverRepository.findById(serverId)
                    .orElseThrow(() -> new RuntimeException("Server not found"));

            Hibernate.initialize(server.getCategories());

            server.getCategories().forEach(category -> {
                Hibernate.initialize(category.getTextChannels());
                Hibernate.initialize(category.getVoiceChannels());
            });

            return mapServerToDetailsResponse(server);

        }
        throw new RuntimeException("User is not a member of this server");
    }

    @Override
    @Transactional
    public TextChannelDto createTextChannel(Long serverId, String channelName, Long requestingUserId, Long categoryId) {
        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new RuntimeException("Server not found"));

        ChannelCategory category = channelCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Channel category not found"));

        if (!category.getServer().getId().equals(serverId)) {
            throw new RuntimeException("Category does not belong to this server");
        }

        // TODO: Na tym etapie pomijamy autoryzację

        // TODO: Ustawianie pozycji nowego kanału (możesz dodać logikę, aby był na końcu)

        Integer nextPosition = category.getTextChannels().size();

        TextChannel channel = TextChannel.builder()
                .name(channelName)
                .server(server)
                .category(category)
                .position(nextPosition)
                .build();

        category.addTextChannel(channel);
        channel = textChannelRepository.save(channel);

        return mapTextChannelToDto(channel);
    }

    @Override
    @Transactional
    public VoiceChannelDto createVoiceChannel(Long serverId, String channelName, Long requestingUserId, Long categoryId) {
        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new RuntimeException("Server not found"));

        ChannelCategory category = channelCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Channel category not found"));

        if (!category.getServer().getId().equals(serverId)) {
            throw new RuntimeException("Category does not belong to this server");
        }

        // TODO: Autoryzacja
        Integer nextPosition = category.getVoiceChannels().size();

        VoiceChannel channel = VoiceChannel.builder()
                .name(channelName)
                .server(server)
                .category(category)
                .position(nextPosition)
                .build();

        category.addVoiceChannel(channel);
        channel = voiceChannelRepository.save(channel);

        return mapVoiceChannelToDto(channel);
    }

    @Override
    @Transactional
    public ChannelCategoryDto createCategory(Long serverId, String categoryName, Long requestingUserId) {
        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new RuntimeException("Server not found"));

        // TODO: Autoryzacja: tylko właściciel/admin może tworzyć kategorie
        if (!server.getOwnerId().equals(requestingUserId)) {
            throw new RuntimeException("User not authorized to create categories on this server");
        }

        // Ustaw pozycję nowej kategorii na końcu
        Integer nextPosition = server.getCategories().size();

        ChannelCategory category = ChannelCategory.builder()
                .name(categoryName)
                .server(server)
                .position(nextPosition)
                .textChannels(new ArrayList<>())
                .voiceChannels(new ArrayList<>())
                .build();
        server.addCategory(category);
        category = channelCategoryRepository.save(category);

        return mapCategoryToDto(category);
    }

    // --- PRYWATNE METODY MAPUJĄCE DTO ---
    private ServerDetailsResponse mapServerToDetailsResponse(Server server) {
        ServerDetailsResponse response = ServerDetailsResponse.builder()
                .id(server.getId())
                .name(server.getName())
                .iconUrl(server.getIconUrl())
                .ownerId(server.getOwnerId())
                .build();

        List<ChannelCategoryDto> categoryDtos = server.getCategories().stream()
                .map(this::mapCategoryToDto)
                .collect(Collectors.toList());
        response.setCategories(categoryDtos);

        return response;
    }

    private ChannelCategoryDto mapCategoryToDto(ChannelCategory category) {
        ChannelCategoryDto dto = ChannelCategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .position(category.getPosition())
                .build();

        List<TextChannelDto> textChannelDtos = category.getTextChannels().stream()
                .map(this::mapTextChannelToDto)
                .collect(Collectors.toList());
        dto.setTextChannels(textChannelDtos);

        List<VoiceChannelDto> voiceChannelDtos = category.getVoiceChannels().stream()
                .map(this::mapVoiceChannelToDto)
                .collect(Collectors.toList());
        dto.setVoiceChannels(voiceChannelDtos);

        return dto;
    }

    private TextChannelDto mapTextChannelToDto(TextChannel channel) {
        return TextChannelDto.builder()
                .id(channel.getId())
                .name(channel.getName())
                .position(channel.getPosition())
                .build();
    }

    private VoiceChannelDto mapVoiceChannelToDto(VoiceChannel channel) {
        return VoiceChannelDto.builder()
                .id(channel.getId())
                .name(channel.getName())
                .position(channel.getPosition())
                .build();
    }


}
