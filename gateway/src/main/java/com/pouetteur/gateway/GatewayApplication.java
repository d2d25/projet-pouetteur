package com.pouetteur.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        String prefix = "/pouetteur/v1";
        return builder.routes()
                .route("auth-service", r -> r.path(prefix + "/api/auth/**")
                        .filters(f -> f.rewritePath(prefix + "/api/auth/(?<remains>.*)", "/auth/${remains}")
                                .preserveHostHeader())
                        .uri("lb://auth-service"))
                .route("auth-service", r -> r.path(prefix + "/api/profiles/**")
                        .filters(f -> f.rewritePath(prefix + "/api/profiles/(?<remains>.*)", "/profiles/${remains}")
                                .preserveHostHeader())
                        .uri("lb://auth-service"))
                .route("pouet-service", r -> r.path(prefix + "/api/pouets/**")
                        .filters(f -> f.rewritePath(prefix + "/api/pouets/(?<remains>.*)", "/api/pouets/${remains}")
                                .preserveHostHeader())
                        .uri("lb://pouet-service"))
                .route("messaging-service", r -> r.path(prefix + "/api/messages/**")
                        .filters(f -> f.rewritePath(prefix + "/api/messages/(?<remains>.*)", "/messaging/${remains}")
                                .preserveHostHeader())
                        .uri("lb://messaging-service"))
                .route("notification-service", r -> r.path(prefix + "/api/notifications/**")
                        .filters(f -> f.rewritePath(prefix + "/api/notifications/(?<remains>.*)", "/notifications/${remains}")
                                .preserveHostHeader())
                        .uri("lb://notification-service"))
                .build();
    }
}
