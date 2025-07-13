package com.discrowd.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private final AuthCookieToAuthHeaderFilter authCookieToAuthHeaderFilter;

    public GatewayConfig(AuthCookieToAuthHeaderFilter authCookieToAuthHeaderFilter) {
        this.authCookieToAuthHeaderFilter = authCookieToAuthHeaderFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("server_service", r -> r.path("/server-service/**")
                        .filters(f -> f.rewritePath("/server-service/(?<remaining>.*)", "/${remaining}")
                                .filter(authCookieToAuthHeaderFilter.apply(new AuthCookieToAuthHeaderFilter.Config())))
                        .uri("http://localhost:8081"))
                // ... inne Twoje trasy
                .build();
    }
}