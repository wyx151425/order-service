package com.polarbookshop.orderservice.order.domain;

import com.polarbookshop.orderservice.book.Book;
import com.polarbookshop.orderservice.book.BookClient;
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

    private final BookClient bookClient;
    private final OrderRepository orderRepository;

    public OrderService(BookClient bookClient, OrderRepository orderRepository) {
        this.bookClient = bookClient;
        this.orderRepository = orderRepository;
    }

    // 使用 Flux 来发布多个订单
    public Flux<Order> getAllOrders() {
        return orderRepository.findAll();
    }

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
                // 保存订单 (可能是 accepted 状态或 rejected 状态)
                .flatMap(orderRepository::save);
    }

    public static Order buildAcceptedOrder(Book book, int quantity) {
        // 当订单被接受时，指定 ISBN、图书名称 (书名+作者)、数量和状态。Spring Data 会负责添加标识符，版本和审计元数据
        return Order.of(book.isbn(), book.title() + " - " + book.author(), book.price(), quantity, OrderStatus.ACCEPTED);
    }

    public static Order buildRejectedOrder(String bookIsbn, int quantity) {
        // 当订单被拒绝时，只需声明 ISBN，数量和状态即可。Spring Data 将负责添加标识符，版本和审计元数据信息
        return Order.of(bookIsbn, null, null, quantity, OrderStatus.REJECTED);
    }
}
