package com.polarbookshop.orderservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;
import java.net.URI;

/**
 * @Author: WangZhenqi
 * @Description: 声明配置 Catalog Service URI 的自定义属性
 * @Date: Created in 2025-11-01 10:04
 * @Modified By:
 */
// 自定义属性的前缀
@ConfigurationProperties(prefix = "polar")
public record ClientProperties(

        // 声明 Catalog Service URI 的属性，它不能为空
        @NotNull
        URI catalogServiceUri
) {
}
