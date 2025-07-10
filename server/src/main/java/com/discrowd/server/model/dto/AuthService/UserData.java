package com.discrowd.server.model.dto.AuthService;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserData {
    private String username;
    private String profileImageUrl;
}