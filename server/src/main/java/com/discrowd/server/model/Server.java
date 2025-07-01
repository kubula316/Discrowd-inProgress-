package com.discrowd.server.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "servers")
public class Server {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String iconUrl = "https://developers.elementor.com/docs/assets/img/elementor-placeholder-image.png";
    private Long ownerId;


    @OneToMany(mappedBy = "server", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TextChannel> textChannels = new ArrayList<>();


    @OneToMany(mappedBy = "server", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoiceChannel> voiceChannels = new ArrayList<>();

    public Server(String name, String description, Long ownerId) {
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
        this.iconUrl = "https://developers.elementor.com/docs/assets/img/elementor-placeholder-image.png"; // Domyślny URL ikony
    }
    public void addTextChannel(TextChannel channel) {
        this.textChannels.add(channel);
        channel.setServer(this);
    }
    public void addVoiceChannel(VoiceChannel channel) {
        this.voiceChannels.add(channel);
        channel.setServer(this);
    }
}

