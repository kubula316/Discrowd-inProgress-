package com.discrowd.server.service;

import com.discrowd.server.model.dto.ChannelCategoryDto;
import com.discrowd.server.model.dto.ServerDetailsResponse;
import com.discrowd.server.model.dto.TextChannelDto;
import com.discrowd.server.model.dto.VoiceChannelDto;
import com.discrowd.server.model.entity.*;
import com.discrowd.server.model.response.UserServerResponse;
import com.discrowd.server.model.response.ServerResponse;
import com.discrowd.server.model.response.UserServerMembershipResponse;

import java.util.List;

public interface ServerService {
    public ServerResponse createServer(String name, Long ownerId);
    public UserServerMembershipResponse joinServer(String serverId, Long userId);
    public void leaveServer(String serverId, Long userId);
    public List<UserServerResponse> getUserServers(Long userId);
    public ServerDetailsResponse getServerDetails(String serverId, Long userId);
    public ServerDetailsResponse createTextChannel(String serverId, String channelName, Long requestingUserId, String categoryId);
    public ServerDetailsResponse createVoiceChannel(String serverId, String channelName, Long requestingUserId, String categoryId);
    public ServerDetailsResponse createCategory(String serverId, String categoryName, Long requestingUserId);

}
