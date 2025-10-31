package com.polarbookshop.orderservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;

/**
 * @Author: WangZhenqi
 * @Description:
 * @Date: Created in 2025-10-31 23:05
 * @Modified By:
 */
// 表明该类为 Spring 的配置员
@Configuration
// 为持久化实体启用 R2DBC 审计
@EnableR2dbcAuditing
public class DataConfig {
}
