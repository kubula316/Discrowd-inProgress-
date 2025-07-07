package com.discrowd.server.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "channel_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChannelCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id", nullable = false)
    private Server server;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<TextChannel> textChannels = new ArrayList<>();


    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<VoiceChannel> voiceChannels = new ArrayList<>();

    private Integer position;

    public ChannelCategory(String name, Server server, Integer position) {
        this.name = name;
        this.server = server;
        this.position = position;
    }

    public void addTextChannel(TextChannel channel) {
        this.textChannels.add(channel);
        channel.setCategory(this);
    }

    public void addVoiceChannel(VoiceChannel channel) {
        this.voiceChannels.add(channel);
        channel.setCategory(this);
    }
}
