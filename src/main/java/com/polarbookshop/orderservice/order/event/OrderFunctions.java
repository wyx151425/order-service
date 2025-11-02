package com.polarbookshop.orderservice.order.event;

import com.polarbookshop.orderservice.order.domain.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

/**
 * @Author: WangZhenqi
 * @Description: 消费来自 RabbitMQ 的消息
 * @Date: Created in 2025-11-02 10:34
 * @Modified By:
 */
@Configuration
public class OrderFunctions {

    private static final Logger log = LoggerFactory.getLogger(OrderFunctions.class);

    @Bean
    public Consumer<Flux<OrderDispatchedMessage>> dispatchOrder(OrderService orderService) {
        // 对于派发的每条消息，它都会更新数据库中相关订单的状态
        return flux -> orderService.consumeOrderDispatchedEvent(flux)
                // 对于数据库中要更新的每个订单，均打印一条日志消息
                .doOnNext(order -> log.info("The order with id {} is dispatched", order.id()))
                // 订阅反应式流以激活它。如果没有订阅者，流中不会产生数据流
                .subscribe();
    }
}
