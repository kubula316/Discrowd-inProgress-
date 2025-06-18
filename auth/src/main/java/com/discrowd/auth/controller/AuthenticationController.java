package com.discrowd.auth.controller;

import com.discrowd.auth.model.AuthenticationRequest;
import com.discrowd.auth.model.AuthenticationResponse;
import com.discrowd.auth.model.RefreshRequest;
import com.discrowd.auth.model.RegisterRequest;
import com.discrowd.auth.security.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody RefreshRequest refreshToken){
        String token = refreshToken.getRefreshToken();
        return authenticationService.refreshToken(token);
    }

    @GetMapping("/validate-token")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);
        boolean isValid = authenticationService.isTokenValid(token);
        return ResponseEntity.ok(isValid);
    }
}