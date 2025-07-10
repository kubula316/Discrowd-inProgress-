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
public class ChannelCategoryDto {
    private String id;
    private String name;
    private Integer position;
    private List<TextChannelDto> textChannels;
    private List<VoiceChannelDto> voiceChannels;
}
