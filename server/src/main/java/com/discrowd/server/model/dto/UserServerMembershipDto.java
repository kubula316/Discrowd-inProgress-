package com.discrowd.server.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserServerMembershipDto {
    private String id;
    private Long userId;
    private String role;
    private String nickname;
    private String avatarUrl;
}
