package com.discrowd.gateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUserIdExtractorFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret-key}")
    private String jwtSecretKey;

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // --- KLUCZOWA ZMIANA: Pomiń logikę filtra dla ścieżek WebSocket ---
        // Frontend łączy się z /server-service/ws/server
        if (path.startsWith("/server-service/ws/server")) {
            return chain.filter(exchange); // Po prostu przepuść żądanie dalej bez przetwarzania JWT
        }
        // --- KONIEC KLUCZOWEJ ZMIANY ---


        String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);

            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(getSignInKey())
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                if (claims.getExpiration().before(new Date())) {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }

                Long userId = claims.get("id", Long.class);
                if (userId == null) {
                    try {
                        userId = Long.parseLong(claims.getSubject());
                    } catch (NumberFormatException e) {
                        exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                        return exchange.getResponse().setComplete();
                    }
                }

                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-User-Id", String.valueOf(userId))
                        .build();

                System.out.println("JWT Extracted User ID: " + userId + " for path: " + path); // Dodaj logowanie
                return chain.filter(exchange.mutate().request(modifiedRequest).build());

            } catch (Exception e) {
                System.err.println("JWT validation failed for path: " + path + " Error: " + e.getMessage()); // Dodaj logowanie błędów
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        }

        // Jeśli brak nagłówka Authorization i nie jest to ścieżka WebSocket, przejdź dalej
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
