package com.discrowd.gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AuthCookieToAuthHeaderFilter extends AbstractGatewayFilterFactory<AuthCookieToAuthHeaderFilter.Config> {

    private static final Pattern TOKEN_COOKIE_PATTERN = Pattern.compile("token=([^;]+)");

    public AuthCookieToAuthHeaderFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            List<String> cookieHeaders = request.getHeaders().get(HttpHeaders.COOKIE);

            String jwtToken = null;
            if (cookieHeaders != null && !cookieHeaders.isEmpty()) {
                for (String cookieHeader : cookieHeaders) {
                    Matcher matcher = TOKEN_COOKIE_PATTERN.matcher(cookieHeader);
                    if (matcher.find()) {
                        jwtToken = matcher.group(1);
                        break;
                    }
                }
            }

            if (jwtToken != null) {
                // Dodaj nagłówek Authorization do żądania
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .build();
                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            } else {
                // Jeśli tokena nie ma w cookie, sprawdź czy jest w nagłówku Authorization (np. od innych klientów)
                if (request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    return chain.filter(exchange); // Jeśli jest Authorization, idź dalej
                }
                // Jeśli nie ma ani w Cookie, ani w Authorization, odrzuć
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };
    }

    public static class Config {

    }
}
