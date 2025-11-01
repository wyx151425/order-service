package com.polarbookshop.orderservice.order.web;

import com.polarbookshop.orderservice.order.domain.Order;
import com.polarbookshop.orderservice.order.domain.OrderService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

/**
 * @Author: WangZhenqi
 * @Description: 定义处理 REST 请求的处理器
 * @Date: Created in 2025-11-01 9:32
 * @Modified By:
 */
// 构造型注解，标注该类为 Spring 组件并且要作为 REST 端点处理器的源
@RestController
// 声明根路径映射 URI (/orders)，该类将会为此路径提供处理器
@RequestMapping("orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 使用 Flux 来发布多个订单 (0..N)
    @GetMapping
    public Flux<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    // 接受 OrderRequest 对象，对其进行校验并使用它来创建订单。创建的订单将会以 Mono 的形式返回
    @PostMapping
    public Mono<Order> submitOrder(@RequestBody @Valid OrderRequest orderRequest) {
        return orderService.submitOrder(orderRequest.isbn(), orderRequest.quantity());
    }
}
