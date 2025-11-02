package com.polarbookshop.orderservice.order.event;

/**
 * @Author: WangZhenqi
 * @Description: 用来表示已派送订单事件的 DTO
 * @Date: Created in 2025-11-02 10:33
 * @Modified By:
 */
public record OrderDispatchedMessage(
        Long orderId
) {
}
