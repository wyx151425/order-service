package com.polarbookshop.orderservice.book;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

/**
 * @Author: WangZhenqi
 * @Description: 测试与 Catalog Service 应用的交互
 * @Date: Created in 2025-11-01 10:51
 * @Modified By:
 */
@TestMethodOrder(MethodOrderer.Random.class)
class BookClientTests {

    private MockWebServer mockWebServer;
    private BookClient bookClient;

    @BeforeEach
    void setup() throws IOException {
        this.mockWebServer = new MockWebServer();
        // 在运行测试用例前启动 mock 服务器
        this.mockWebServer.start();

        // 使用 mock 服务器的 URL 作为 WebClient 的基础 URL
        var webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").uri().toString())
                .build();
        this.bookClient = new BookClient(webClient);
    }

    @AfterEach
    void clean() throws IOException {
        // 在完成测试后关闭 mock 服务器
        this.mockWebServer.shutdown();
    }

    @Test
    void whenBookExistsThenReturnBook() {
        var bookIsbn = "1234567890";

        // 定义 mock 服务器返回的相应
        var mockResponse = new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("""
                        	{
                        		"isbn": %s,
                        		"title": "Title",
                        		"author": "Author",
                        		"price": 9.90,
                        		"publisher": "Polarsophia"
                        	}
                        """.formatted(bookIsbn));

        // 添加 mock 响应到 mock 服务器处理的队列中
        mockWebServer.enqueue(mockResponse);

        Mono<Book> book = bookClient.getBookByIsbn(bookIsbn);

        // 使用 BookClient 返回的对象来初始化一个 StepVerifier 对象
        StepVerifier.create(book)
                // 断言返回的 Book 具有所请求的 ISBN
                .expectNextMatches(b -> b.isbn().equals(bookIsbn))
                // 检查反应式流成功完成
                .verifyComplete();
    }

    @Test
    void whenBookNotExistsThenReturnEmpty() {
        var bookIsbn = "1234567891";

        var mockResponse = new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(404);

        mockWebServer.enqueue(mockResponse);

        StepVerifier.create(bookClient.getBookByIsbn(bookIsbn))
                .expectNextCount(0)
                .verifyComplete();
    }

}
