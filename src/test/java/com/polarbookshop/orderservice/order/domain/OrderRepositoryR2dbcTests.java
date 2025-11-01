package com.polarbookshop.orderservice.order.domain;

import com.polarbookshop.orderservice.config.DataConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.test.StepVerifier;

/**
 * @Author: WangZhenqi
 * @Description: 数据 R2DBC 切片的集成测试
 * @Date: Created in 2025-11-01 11:02
 * @Modified By:
 */
// 标记该测试类主要关注 R2BDC 组件
@DataR2dbcTest
// 导入所需的 R2DBC 配置，以启用数据审计功能
@Import(DataConfig.class)
// 激活测试容器的自动化启动和清理
@Testcontainers
class OrderRepositoryR2dbcTests {

    // 标记用于测试的 PostgreSQL 容器
    @Container
    static PostgreSQLContainer<?> postgresql = new PostgreSQLContainer<>(DockerImageName.parse("postgres:14.12"));

    @Autowired
    private OrderRepository orderRepository;

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        // 重写 R2DBC 和 Flyway 配置以指向测试 PostgreSQL 实例
        registry.add("spring.r2dbc.url", OrderRepositoryR2dbcTests::r2dbcUrl);
        registry.add("spring.r2dbc.username", postgresql::getUsername);
        registry.add("spring.r2dbc.password", postgresql::getPassword);
        registry.add("spring.flyway.url", postgresql::getJdbcUrl);
    }

    private static String r2dbcUrl() {
        // 构建 R2DBC 连接字符串，因为 Testcontainers 没有像 JDBC 那样提供开箱即用的连接字符串
        return String.format("r2dbc:postgresql://%s:%s/%s", postgresql.getHost(),
                postgresql.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT), postgresql.getDatabaseName());
    }

    @Test
    void findOrderByIdWhenNotExisting() {
        StepVerifier.create(orderRepository.findById(394L))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void createRejectedOrder() {
        var rejectedOrder = OrderService.buildRejectedOrder("1234567890", 3);
        StepVerifier
                // 使用 OrderRepository 返回的对象来初始化一个 StepVerifier 对象
                .create(orderRepository.save(rejectedOrder))
                // 断言返回的 Order 具有正确的状态
                .expectNextMatches(order -> order.status().equals(OrderStatus.REJECTED))
                // 检验反应式流成功完成
                .verifyComplete();
    }

}
