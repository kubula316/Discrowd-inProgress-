package com.discrowd.server.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "text_channels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TextChannel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ChannelCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id", nullable = false)
    private Server server;

    private Integer position;

    public TextChannel(String name, ChannelCategory category, Server server, Integer position) {
        this.name = name;
        this.category = category;
        this.server = server;
        this.position = position;
    }
}