package com.discrowd.server.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServerDetailsResponse {
    private String id;
    private String name;
    private String iconUrl;
    private Long ownerId;
    private List<ChannelCategoryDto> categories;
    private List<UserServerMembershipDto> memberships;
}
