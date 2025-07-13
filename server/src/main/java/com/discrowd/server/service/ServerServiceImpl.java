package com.discrowd.server.service;

import com.discrowd.server.model.dto.*;
import com.discrowd.server.model.dto.AuthService.UserData;
import com.discrowd.server.model.entity.Server;
import com.discrowd.server.model.entity.TextChannel;
import com.discrowd.server.model.entity.UserServerMembership;
import com.discrowd.server.model.entity.VoiceChannel;
import com.discrowd.server.model.entity.ChannelCategory;
import com.discrowd.server.model.response.ServerResponse;
import com.discrowd.server.model.response.UserServerMembershipResponse;
import com.discrowd.server.model.response.UserServerResponse;
import com.discrowd.server.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServerServiceImpl implements ServerService{

    private final ServerRepository serverRepository;
    private final AuthServiceClient authServiceClient;

    //TODO za pomocą feigna dodać dane użytkownika do membershipa nickname/avatarUrl



    @Override
    public ServerResponse createServer(String name, Long ownerId) {
        // Tworzenie głównego obiektu serwera
        Server server = Server.builder()
                .name(name)
                .iconUrl("https://developers.elementor.com/docs/assets/img/elementor-placeholder-image.png")
                .ownerId(ownerId)
                .categories(new ArrayList<>()) // Inicjalizacja list
                .memberships(new ArrayList<>()) // Inicjalizacja list
                .build();

        UserData userData = authServiceClient.getUserData(ownerId);

        // Tworzenie membershipa dla właściciela
        UserServerMembership ownerMembership = UserServerMembership.builder()
                .id(UUID.randomUUID().toString())
                .userId(ownerId)
                .role("OWNER")
                .nickname(userData.getUsername())
                .avatarUrl(userData.getProfileImageUrl())
                .build();
        server.addMembership(ownerMembership);

        // Tworzenie pierwszej kategorii (Text Channels)
        ChannelCategory defaultTextCategory = ChannelCategory.builder()
                .id(UUID.randomUUID().toString())
                .name("Text Channels")
                .position(0)
                .textChannels(new ArrayList<>())
                .voiceChannels(new ArrayList<>())
                .build();
        server.addCategory(defaultTextCategory);

        TextChannel generalChannel = TextChannel.builder()
                .id(UUID.randomUUID().toString())
                .name("general")
                .position(0)
                .build();
        defaultTextCategory.addTextChannel(generalChannel);

        // Tworzenie drugiej kategorii (Voice Channels)
        ChannelCategory defaultVoiceCategory = ChannelCategory.builder()
                .id(UUID.randomUUID().toString())
                .name("Voice Channels")
                .position(1)
                .textChannels(new ArrayList<>())
                .voiceChannels(new ArrayList<>())
                .build();
        server.addCategory(defaultVoiceCategory);

        // Tworzenie kanału "General" w drugiej kategorii
        VoiceChannel generalVoiceChannel = VoiceChannel.builder()
                .id(UUID.randomUUID().toString())
                .name("General")
                .position(0)
                .build();
        defaultVoiceCategory.addVoiceChannel(generalVoiceChannel);

        // Zapisujemy cały dokument Server. MongoDB automatycznie zapisze wszystkie osadzone listy.
        server = serverRepository.save(server);

        return ServerResponse.builder()
                .id(server.getId())
                .name(server.getName())
                .iconUrl(server.getIconUrl())
                .ownerId(server.getOwnerId())
                .build();
    }

    @Override
    public UserServerMembershipResponse joinServer(String serverId, Long userId) {

        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new RuntimeException("Server not found"));

        boolean alreadyMember = server.getMemberships().stream()
                .anyMatch(m -> m.getUserId().equals(userId));
        if (alreadyMember) {
            throw new RuntimeException("User is already a member of this server");
        }

        UserData userData = authServiceClient.getUserData(userId);

        // Tworzymy nowy membership
        UserServerMembership membership = UserServerMembership.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .role("MEMBER")
                .nickname(userData.getUsername()) // Placeholder
                .avatarUrl(userData.getProfileImageUrl()) // Placeholder
                .build();

        server.addMembership(membership);

        serverRepository.save(server);

        return UserServerMembershipResponse.builder()
                .membershipId(membership.getId())
                .userId(membership.getUserId())
                .serverId(server.getId())
                .role(membership.getRole())
                .build();
    }

    @Override
    public void leaveServer(String serverId, Long userId) {
        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new RuntimeException("Server not found"));

        // Znajdujemy membership do usunięcia
        Optional<UserServerMembership> membershipOpt = server.getMemberships().stream()
                .filter(m -> m.getUserId().equals(userId))
                .findFirst();

        if (membershipOpt.isEmpty()) {
            throw new RuntimeException("User is not a member of this server");
        }

        if (server.getOwnerId().equals(userId)) {
            serverRepository.delete(server);
            return;
        }

        server.getMemberships().remove(membershipOpt.get());

        serverRepository.save(server);
    }

    @Override
    public List<UserServerResponse> getUserServers(Long userId) {
        List<Server> servers = serverRepository.findByMemberships_UserId(userId);

        return servers.stream()
                .map(server -> {
                    UserServerResponse response = new UserServerResponse();
                    response.setId(server.getId());
                    response.setName(server.getName());
                    response.setIconUrl(server.getIconUrl());
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ServerDetailsResponse getServerDetails(String serverId, Long userId) {

        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new RuntimeException("Server not found"));

        boolean isMember = server.getMemberships().stream()
                .anyMatch(m -> m.getUserId().equals(userId));

        if (!isMember) {
            throw new RuntimeException("User is not a member of this server");
        }

        return mapServerToDetailsResponse(server);
    }

    @Override
    public ServerDetailsResponse createTextChannel(String serverId, String channelName, Long requestingUserId, String categoryId) {
        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new RuntimeException("Server not found"));

        ChannelCategory category = server.getCategories().stream()
                .filter(cat -> cat.getId().equals(categoryId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Channel category not found in this server"));

        // TODO: Na tym etapie pomijamy autoryzację (przenieś z właściciela serwera)

        Integer nextPosition = category.getTextChannels().size();

        TextChannel channel = TextChannel.builder()
                .id(UUID.randomUUID().toString())
                .name(channelName)
                .position(nextPosition)
                .build();

        category.addTextChannel(channel);

        serverRepository.save(server);

        return mapServerToDetailsResponse(server);
    }

    @Override
    public ServerDetailsResponse createVoiceChannel(String serverId, String channelName, Long requestingUserId, String categoryId) {
        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new RuntimeException("Server not found"));

        ChannelCategory category = server.getCategories().stream()
                .filter(cat -> cat.getId().equals(categoryId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Channel category not found in this server"));

        // TODO: Autoryzacja
        Integer nextPosition = category.getVoiceChannels().size();

        VoiceChannel channel = VoiceChannel.builder()
                .id(UUID.randomUUID().toString())
                .name(channelName)
                .position(nextPosition)
                .build();

        category.addVoiceChannel(channel);

        serverRepository.save(server);

        return mapServerToDetailsResponse(server);
    }

    @Override
    public ServerDetailsResponse createCategory(String serverId, String categoryName, Long requestingUserId) {
        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new RuntimeException("Server not found"));

        // TODO: Autoryzacja: tylko właściciel/admin może tworzyć kategorie
        if (!server.getOwnerId().equals(requestingUserId)) {
            throw new RuntimeException("User not authorized to create categories on this server");
        }

        Integer nextPosition = server.getCategories().size();

        ChannelCategory category = ChannelCategory.builder()
                .id(UUID.randomUUID().toString())
                .name(categoryName)
                .position(nextPosition)
                .textChannels(new ArrayList<>())
                .voiceChannels(new ArrayList<>())
                .build();
        server.addCategory(category);

        serverRepository.save(server);

        return mapServerToDetailsResponse(server);
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

        List<UserServerMembershipDto> membershipDtos = server.getMemberships().stream()
                .map(this::mapMembershipToDto)
                .collect(Collectors.toList());
        response.setMemberships(membershipDtos);

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

    private UserServerMembershipDto mapMembershipToDto(UserServerMembership membership) {
        return UserServerMembershipDto.builder()
                .id(membership.getId())
                .userId(membership.getUserId())
                .role(membership.getRole())
                .nickname(membership.getNickname())
                .avatarUrl(membership.getAvatarUrl())
                .build();
    }


}
