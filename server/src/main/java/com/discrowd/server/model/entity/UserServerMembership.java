package com.discrowd.server.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserServerMembership {

    private String id;

    private Long userId;

    private String role;

    private String nickname;

    private String avatarUrl;

}