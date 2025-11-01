package com.polarbookshop.orderservice.book;

/**
 * @Author: WangZhenqi
 * @Description: Book record 是一个存储图书信息的 DTO
 * @Date: Created in 2025-11-01 10:10
 * @Modified By:
 */
public record Book(
        String isbn,
        String title,
        String author,
        Double price
) {
}
