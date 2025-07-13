package com.discrowd.server.security;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ArrayList;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;

    public WebSocketAuthInterceptor(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            List<String> authorization = accessor.getNativeHeader("Authorization");

            if (authorization == null || authorization.isEmpty()) {
                throw new JwtException("Missing authorization header");
            }

            String token = authorization.get(0);
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            } else {
                throw new JwtException("Invalid authorization header format");
            }

            try {
                Jwt jwt = jwtDecoder.decode(token);
                List<GrantedAuthority> authorities = new ArrayList<>();
                Authentication authentication = new JwtAuthenticationToken(jwt, authorities);
                accessor.setUser(authentication);

            } catch (JwtException e) {
                System.err.println("JWT processing failed: " + e.getMessage());
                throw e;
            } catch (Exception e) {
                System.err.println("Unexpected error during WebSocket authentication: " + e.getMessage());
                throw new JwtException("Authentication error");
            }
        }

        return message;
    }
}