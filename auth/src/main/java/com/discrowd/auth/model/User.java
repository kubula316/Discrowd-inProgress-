package com.discrowd.auth.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@SequenceGenerator(name = "seqIdGen", initialValue = 20000, allocationSize = 1)
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqIdGen")
    private Long id;

    @NotBlank
    private String nickname;

    @NotBlank
    @Email
    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private Status status;

    @NotBlank
    private String password;

    private String profileImageUrl = "https://coursesapp.blob.core.windows.net/student-profile-image-container/BlankProfile.png";

    @Enumerated(EnumType.STRING)
    private Role role;









    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public enum Status{
        ACTIVE,
        INACTIVE
    }
}
