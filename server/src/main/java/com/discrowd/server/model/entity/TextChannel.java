package com.discrowd.server.model.entity;

import lombok.*;

@Data
@NoArgsConstructor
@Builder
public class TextChannel {

    private String id;

    private String name;

    private Integer position;

    public TextChannel(String id, String name, Integer position) {
        this.id = id;
        this.name = name;
        this.position = position;
    }
}