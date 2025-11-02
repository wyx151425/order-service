package com.polarbookshop.orderservice.order.event;

/**
 * @Author: WangZhenqi
 * @Description: 表示接受订单事件的 DTO
 * @Date: Created in 2025-11-02 10:47
 * @Modified By:
 */
public record OrderAcceptedMessage(
        Long orderId
) {
}
