package com.polarbookshop.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.integration.annotation.Reactive;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;

/**
 * @Author: WangZhenqi
 * @Description:
 * @Date: Created in 2025-10-31 23:05
 * @Modified By:
 */
// 表明该类为 Spring 的配置源
@Configuration
// 为持久化实体启用 R2DBC 审计
// 在 Spring Data R2DBC 中启用实体审计
@EnableR2dbcAuditing
public class DataConfig {

    @Bean
    // 返回当前认证用户，以便于进行审计
    ReactiveAuditorAware<String> auditorAware() {
        return () ->
                // 从 ReactiveSecurityHolder 中为当前用户提取 SecurityContext 对象
                ReactiveSecurityContextHolder.getContext()
                        // 从 SecurityContext 中为当前认证用户提取 Authentication 对象
                        .map(SecurityContext::getAuthentication)
                        // 处理用户未经认证但尝试操作数据的场景。因为我们保护了所有的端点，所以这种情况永远不应发生，但是为了完整性，我们包含了这种情况
                        .filter(Authentication::isAuthenticated)
                        // 从 Authentication 对象中为当前认证用户提取用户名
                        .map(Authentication::getName);
    }
}
