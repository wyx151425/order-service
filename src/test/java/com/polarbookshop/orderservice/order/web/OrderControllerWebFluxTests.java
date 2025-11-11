package com.polarbookshop.orderservice.order.web;

import com.polarbookshop.orderservice.config.SecurityConfig;
import com.polarbookshop.orderservice.order.domain.Order;
import com.polarbookshop.orderservice.order.domain.OrderService;
import com.polarbookshop.orderservice.order.domain.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * @Author: WangZhenqi
 * @Description: WebFlux 切片的集成测试
 * @Date: Created in 2025-11-01 11:10
 * @Modified By:
 */
// 标识该测试类主要关注 Spring WebFlux 组件，具体来讲，针对的是 OrderController
@WebFluxTest(OrderController.class)
// 导入应用的安全配置
@Import(SecurityConfig.class)
class OrderControllerWebFluxTests {

    // 具有额外特性的 WebClient 变种，会使 RESTful 服务的测试更简便
    @Autowired
    private WebTestClient webClient;

    // 添加 mock OrderService 到 Spring 应用上下文中
    @MockBean
    private OrderService orderService;

    // Mock ReactiveJwtDecoder，这样为了解码访问令牌，应用就不需要尝试调用 Keycloak 以获取公钥了
    @MockBean
    ReactiveJwtDecoder reactiveJwtDecoder;

    @Test
    void whenBookNotAvailableThenRejectOrder() {
        var orderRequest = new OrderRequest("1234567890", 3);
        var expectedOrder = OrderService.buildRejectedOrder(orderRequest.isbn(), orderRequest.quantity());
        given(orderService.submitOrder(orderRequest.isbn(), orderRequest.quantity()))
                // 定义 OrderService mock bean 的预期行为
                .willReturn(Mono.just(expectedOrder));

        webClient
                .mutateWith(SecurityMockServerConfigurers
                        // 修改 HTTP 请求，使其包含一个 JWT 格式的 mock 访问令牌，该令牌具有 “customer” 角色
                        .mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_customer")))
                .post()
                .uri("/orders")
                .bodyValue(orderRequest)
                .exchange()
                // 预期订单创建成功
                .expectStatus().is2xxSuccessful()
                .expectBody(Order.class).value(actualOrder -> {
                    assertThat(actualOrder).isNotNull();
                    assertThat(actualOrder.status()).isEqualTo(OrderStatus.REJECTED);
                });

    }

}
