package com.discrowd.server.model.entity;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChannelCategory {


    private String id;

    private String name;

    private List<TextChannel> textChannels = new ArrayList<>();

    private List<VoiceChannel> voiceChannels = new ArrayList<>();

    private Integer position;


    public ChannelCategory(String id, String name, Integer position) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.textChannels = new ArrayList<>();
        this.voiceChannels = new ArrayList<>();
    }

    // Metody pomocnicze do zarządzania kanałami
    public void addTextChannel(TextChannel channel) {
        if (this.textChannels == null) {
            this.textChannels = new ArrayList<>();
        }
        this.textChannels.add(channel);
    }

    public void addVoiceChannel(VoiceChannel channel) {
        if (this.voiceChannels == null) {
            this.voiceChannels = new ArrayList<>();
        }
        this.voiceChannels.add(channel);
    }
}
