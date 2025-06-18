package com.discrowd.auth.security;

import com.discrowd.auth.model.*;
import com.discrowd.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .profileImageUrl("https://coursesapp.blob.core.windows.net/student-profile-image-container/BlankProfile.png")
                .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user, user.getId());
        var refreshToken = jwtService.generateRefreshToken(user, user.getId());
        return AuthenticationResponse.builder()
                .token(jwtToken).refreshToken(refreshToken).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user, user.getId());
        var refreshToken = jwtService.generateRefreshToken(user, user.getId());
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public ResponseEntity<AuthenticationResponse> refreshToken(String refreshToken) {
        if (jwtService.isTokenExpired(refreshToken)){
            System.out.println("REFRESH TOKEN NIEWAZNY LmAo");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        var userId = jwtService.extractUserId(refreshToken);
        var user = userRepository.findById(userId).orElseThrow();

        var newJwtToken = jwtService.generateToken(user, userId);
        var newRefreshToken = jwtService.generateRefreshToken(user, userId);
        return ResponseEntity.ok(AuthenticationResponse.builder()
                .token(newJwtToken)
                .refreshToken(newRefreshToken)
                .build());
    }


    public boolean isTokenValid(String token) {
        return !jwtService.isTokenExpired(token);
    }
}
