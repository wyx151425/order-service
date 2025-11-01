package com.polarbookshop.orderservice.book;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

/**
 * @Author: WangZhenqi
 * @Description: 使用 WebClient 定义反应式 REST 客户端
 * @Date: Created in 2025-11-01 10:10
 * @Modified By:
 */

@Component
public class BookClient {

	private static final String BOOKS_ROOT_API = "/books/";

    private final WebClient webClient;

	public BookClient(WebClient webClient) {
        // 前文配置的 WebClient bean
		this.webClient = webClient;
	}

	public Mono<Book> getBookByIsbn(String isbn) {
		return webClient
                // 请求应该使用 GET 方法
				.get()
				// 请求的目标 URL 是 /books/{isbn}
                .uri(BOOKS_ROOT_API + isbn)
				// 发送请求并获取响应
                .retrieve()
                // 以 Mono<Book> 的形式返回要检索的对象
				.bodyToMono(Book.class)
				.timeout(Duration.ofSeconds(3), Mono.empty())
				.onErrorResume(WebClientResponseException.NotFound.class, exception -> Mono.empty())
				.retryWhen(Retry.backoff(3, Duration.ofMillis(100)))
				.onErrorResume(Exception.class, exception -> Mono.empty());
	}

}
