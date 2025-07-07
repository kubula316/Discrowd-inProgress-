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
    public UserServerMembershipResponse joinServer(Long serverId, Long userId);
    public void leaveServer(Long serverId, Long userId);
    public List<UserServerResponse> getUserServers(Long userId);
    public ServerDetailsResponse getServerDetails(Long serverId, Long userId);
    public TextChannelDto createTextChannel(Long serverId, String channelName, Long requestingUserId, Long categoryId);
    public VoiceChannelDto createVoiceChannel(Long serverId, String channelName, Long requestingUserId, Long categoryId);
    public ChannelCategoryDto createCategory(Long serverId, String categoryName, Long requestingUserId);

}
