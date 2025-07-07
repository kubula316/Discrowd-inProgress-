package com.discrowd.server.model.response;

import lombok.Data;

@Data
public class UserServerResponse {

    private Long id;
    private String name;
    private String iconUrl = "https://developers.elementor.com/docs/assets/img/elementor-placeholder-image.png";

}
