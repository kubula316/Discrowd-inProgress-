package com.discrowd.server.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    private final JwtDecoder jwtDecoder;

    // Pattern do wyodrębniania tokena z ciągu cookie
    private static final Pattern TOKEN_COOKIE_PATTERN = Pattern.compile("token=([^;]+)");


    public CustomHandshakeHandler(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        List<String> cookieHeaders = request.getHeaders().get("Cookie");
        String jwtString = null;
        if (cookieHeaders != null && !cookieHeaders.isEmpty()) {
            for (String cookieHeader : cookieHeaders) {
                Matcher matcher = TOKEN_COOKIE_PATTERN.matcher(cookieHeader);
                if (matcher.find()) {
                    jwtString = matcher.group(1); // Znaleziono token
                    break;
                }
            }
        }

        if (jwtString != null) {
            try {
                Jwt jwt = jwtDecoder.decode(jwtString);
                JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt);
                return authentication;

            } catch (JwtException e) {
                System.err.println("CustomHandshakeHandler: Błąd dekodowania JWT z Cookie: " + e.getMessage());
                return null;
            }
        }
        System.out.println("CustomHandshakeHandler: Brak tokena 'token' w nagłówkach Cookie. Użytkownik anonimowy.");
        return null;
    }
}
