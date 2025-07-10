package com.discrowd.server.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class VoiceChannel {

    private String id;

    private String name;

    private Integer position;

    public VoiceChannel(String id, String name, Integer position) {
        this.id = id;
        this.name = name;
        this.position = position;
    }
}
