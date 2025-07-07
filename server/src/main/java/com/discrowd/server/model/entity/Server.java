package com.discrowd.server.model.entity;

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
    private String iconUrl = "https://developers.elementor.com/docs/assets/img/elementor-placeholder-image.png";
    private Long ownerId;

    @OneToMany(mappedBy = "server", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<ChannelCategory> categories = new ArrayList<>();

    public Server(String name, Long ownerId) {
        this.name = name;
        this.ownerId = ownerId;
        this.iconUrl = "https://developers.elementor.com/docs/assets/img/elementor-placeholder-image.png";
    }

    public void addCategory(ChannelCategory category) {
        this.categories.add(category);
        category.setServer(this);
    }
}

