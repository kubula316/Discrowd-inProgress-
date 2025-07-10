package com.discrowd.server.model.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "guilds")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Server {

    @Id
    private String id;

    private String name;
    private String iconUrl = "https://developers.elementor.com/docs/assets/img/elementor-placeholder-image.png";
    private Long ownerId;

    private List<ChannelCategory> categories = new ArrayList<>();

    private List<UserServerMembership> memberships = new ArrayList<>();

    public Server(String name, Long ownerId) {
        this.name = name;
        this.ownerId = ownerId;
        this.iconUrl = "https://developers.elementor.com/docs/assets/img/elementor-placeholder-image.png";
        this.categories = new ArrayList<>();
        this.memberships = new ArrayList<>();
    }

    public void addCategory(ChannelCategory category) {
        if (this.categories == null) {
            this.categories = new ArrayList<>();
        }
        this.categories.add(category);
    }

    public void addMembership(UserServerMembership membership) {
        if (this.memberships == null) {
            this.memberships = new ArrayList<>();
        }
        this.memberships.add(membership);
    }
}

