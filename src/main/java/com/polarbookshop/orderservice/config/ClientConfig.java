package com.polarbookshop.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @Author: WangZhenqi
 * @Description: 配置 WebClient bean 以调用 Catalog Service
 * @Date: Created in 2025-11-01 10:25
 * @Modified By:
 */
@Configuration
public class ClientConfig {

    @Bean
    WebClient webClient(
            ClientProperties clientProperties,
            // Spring Boot 自动配置的对象，以构建 Web Client bean
            WebClient.Builder webClientBuilder
    ) {
        // 将 WebClient 基础 URL 配置为自定义属性所声明的 Catalog Service URL
        return webClientBuilder
                .baseUrl(clientProperties.catalogServiceUri().toString())
                .build();
    }

}

