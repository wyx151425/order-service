package com.polarbookshop.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;

/**
 * @Author: WangZhenqi
 * @Description: 为 Order Service 配置安全策略和 JWT 认证
 * @Date: Created in 2025-11-11 21:11
 * @Modified By:
 */
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        return http
                // 所有请求均需要认证
                .authorizeExchange(exchange -> exchange.anyExchange().authenticated())
                // 使用基于 JWT (即 JWT 认证) 的默认配置启用 OAuth2 资源服务器
                .oauth2ResourceServer(ServerHttpSecurity.OAuth2ResourceServerSpec::jwt)
                // 每个请求必须包含访问令牌，所以没有必要在不同的请求间保持用户会话。我们希望它是无状态的
                .requestCache(requestCacheSpec -> requestCacheSpec.requestCache(NoOpServerRequestCache.getInstance()))
                // 因为认证策略是无状态的，并不涉及基于浏览器的客户端，所以我们可以安全地禁用 CSRF 防护
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }
}
