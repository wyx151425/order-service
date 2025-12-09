package com.polarbookshop.orderservice.order.domain;

import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

/**
 * @Author: WangZhenqi
 * @Description: 定义领域和持久化实体的 Order record
 * @Date: Created in 2025-10-31 22:53
 * @Modified By:
 */
// 配置 Order 对象和 orders 表之间的映射
@Table("orders")
public record Order(

        // 实体的主键
        @Id
        Long id,

        String bookIsbn,
        String bookName,
        Double bookPrice,
        Integer quantity,
        OrderStatus status,

        // 实体的创建时间
        @CreatedDate
        Instant createdDate,

        // 实体的最后修改时间
        @LastModifiedDate
        Instant lastModifiedDate,

        // 创建实体的用户
        @CreatedBy
        String createdBy,

        // 最后修改实体的用户
        @LastModifiedBy
        String lastModifiedBy,

        // 实体的版本号
        @Version
        int version
) {
    public static Order of(String bookIsbn, String bookName, Double bookPrice, Integer quantity, OrderStatus status) {
        return new Order(null, bookIsbn, bookName, bookPrice, quantity, status, null, null, null, null, 0);
    }
}
