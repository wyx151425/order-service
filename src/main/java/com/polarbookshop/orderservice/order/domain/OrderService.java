package com.polarbookshop.orderservice.order.domain;

import com.polarbookshop.orderservice.book.Book;
import com.polarbookshop.orderservice.book.BookClient;
import com.polarbookshop.orderservice.order.event.OrderAcceptedMessage;
import com.polarbookshop.orderservice.order.event.OrderDispatchedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final BookClient bookClient;
    private final OrderRepository orderRepository;
    private final StreamBridge streamBridge;

    public OrderService(BookClient bookClient, StreamBridge streamBridge, OrderRepository orderRepository) {
        this.bookClient = bookClient;
        this.streamBridge = streamBridge;
        this.orderRepository = orderRepository;
    }

    // 使用 Flux 来发布多个订单
    public Flux<Order> getAllOrders(String userId) {
        // 当请求所有订单的时候，响应中只包含属于给定用户的订单
        return orderRepository.findAllByCreatedBy(userId);
    }

    // 在本地事务中执行方法
    @Transactional
    public Mono<Order> submitOrder(String isbn, int quantity) {
//        // 基于 Order 对象创建 Mono
//        return Mono.just(buildRejectedOrder(isbn, quantity))
//                // 将上一步通过反应式流生成的 Order 对象保存到数据库中
//                .flatMap(orderRepository::save);
        return bookClient.getBookByIsbn(isbn)
                // 如果图书可用，则接受该订单
                .map(book -> buildAcceptedOrder(book, quantity))
                // 如果图书不可用，则拒绝该订单
                .defaultIfEmpty(buildRejectedOrder(isbn, quantity))
                // 保存订单 (可能是 accepted 状态或 rejected 状态) 到数据库中
                .flatMap(orderRepository::save)
                // 如果订单被接受，则发布一个事件
                .doOnNext(this::publishOrderAcceptedEvent);
    }

    public static Order buildAcceptedOrder(Book book, int quantity) {
        // 当订单被接受时，指定 ISBN、图书名称 (书名+作者)、数量和状态。Spring Data 会负责添加标识符，版本和审计元数据
        return Order.of(book.isbn(), book.title() + " - " + book.author(), book.price(), quantity, OrderStatus.ACCEPTED);
    }

    public static Order buildRejectedOrder(String bookIsbn, int quantity) {
        // 当订单被拒绝时，只需声明 ISBN，数量和状态即可。Spring Data 将负责添加标识符，版本和审计元数据信息
        return Order.of(bookIsbn, null, null, quantity, OrderStatus.REJECTED);
    }

    private void publishOrderAcceptedEvent(Order order) {
        // 如果订单没有被接受，不执行任何操作
        if (!order.status().equals(OrderStatus.ACCEPTED)) {
            return;
        }
        // 构建一条消息以通知该订单已被接受
        var orderAcceptedMessage = new OrderAcceptedMessage(order.id());
        log.info("Sending order accepted event with id: {}", order.id());
        // 将消息显示发送至 acceptOrder-out-0 绑定
        var result = streamBridge.send("acceptOrder-out-0", orderAcceptedMessage);
        log.info("Result of sending data for order with id {}: {}", order.id(), result);
    }

    public Flux<Order> consumeOrderDispatchedEvent(Flux<OrderDispatchedMessage> flux) {
        return flux
                // 对于发布到流中的每个对象，从数据库中读取相关的订单
                .flatMap(message -> orderRepository.findById(message.orderId()))
                // 将订单更新为 “dispatched” 状态
                .map(this::buildDispatchedOrder)
                // 将更新后的订单保存到数据库中
                .flatMap(orderRepository::save);
    }

    private Order buildDispatchedOrder(Order existingOrder) {
        // 对于给定的订单，返回一个状态为 “dispatched” 的新记录
        return new Order(
                existingOrder.id(),
                existingOrder.bookIsbn(),
                existingOrder.bookName(),
                existingOrder.bookPrice(),
                existingOrder.quantity(),
                OrderStatus.DISPATCHED,
                existingOrder.createdDate(),
                existingOrder.lastModifiedDate(),
                // 创建实体的用户
                existingOrder.createdBy(),
                // 最后更新实体的用户
                existingOrder.lastModifiedBy(),
                existingOrder.version()
        );
    }
}
