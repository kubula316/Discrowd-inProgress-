package com.discrowd.server.service;

import com.discrowd.server.model.Server;
import com.discrowd.server.model.TextChannel;
import com.discrowd.server.model.UserServerMembership;
import com.discrowd.server.model.dto.response.UserServerResponse;

import java.util.List;
import java.util.Optional;

public interface ServerService {
    public Server createServer(String name, String description, Long ownerId);
    public UserServerMembership joinServer(Long serverId, Long userId);
    public void leaveServer(Long serverId, Long userId);
    public List<UserServerResponse> getUserServers(Long userId);
    public Server getServerDetails(Long serverId, Long userId);
    public TextChannel createTextChannel(Long serverId, String channelName, Long requestingUserId);

}
