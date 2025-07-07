package com.discrowd.server.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class ServerDto {
    private Long id;
    private String name;
    private String description;
    private String iconUrl;
    private Long ownerId;
    private List<ChannelCategoryDto> categories;
}






