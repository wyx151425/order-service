package com.polarbookshop.orderservice.order.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

/**
 * @Author: WangZhenqi
 * @Description: 访问订单的资源库接口
 * @Date: Created in 2025-10-31 23:06
 * @Modified By:
 */
// 扩展提供 CRUD 操作的反应式资源库，声明要管理的实体类型 (Order) 及其主键的类型 (Long)
public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {
    // 自定义方法，用于查询给定用户创建的订单
    Flux<Order> findAllByCreatedBy(String userId);
}
