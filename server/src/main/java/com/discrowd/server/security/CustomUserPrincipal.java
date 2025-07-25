package com.discrowd.server.security;

import java.security.Principal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomUserPrincipal implements Principal {
    private String id;
    private String username;

    @Override
    public String getName() {
        return this.id; // KLUCZOWE: Zwracamy prawdziwe ID użytkownika jako "nazwę" Principal
    }
}
