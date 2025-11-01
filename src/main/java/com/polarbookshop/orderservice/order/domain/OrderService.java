package com.polarbookshop.orderservice.order.domain;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @Author: WangZhenqi
 * @Description: 以反应式流的方式读取订单
 * @Date: Created in 2025-11-01 9:13
 * @Modified By:
 */
// 构造型注解，将此类标记为 Spring 管理的服务
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // 使用 Flux 来发布多个订单
    public Flux<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Mono<Order> submitOrder(String isbn, int quantity) {
        // 基于 Order 对象创建 Mono
        return Mono.just(buildRejectedOrder(isbn, quantity))
                // 将上一步通过反应式流生成的 Order 对象保存到数据库中
                .flatMap(orderRepository::save);
    }

    public static Order buildRejectedOrder(String bookIsbn, int quantity) {
        // 当订单被拒绝时，只需声明 ISBN，数量和状态即可。Spring Data 将负责添加标识符，版本和审计元数据信息
        return Order.of(bookIsbn, null, null, quantity, OrderStatus.REJECTED);
    }
}
