package com.polarbookshop.orderservice.order.web;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author: WangZhenqi
 * @Description: 为每个字段均定义了校验限制的 OrderRequest DTO 类
 * @Date: Created in 2025-11-01 9:30
 * @Modified By:
 */
public record OrderRequest(

        // 不允许为 null 并且必须包含至少一个非空字符
        @NotBlank(message = "The book ISBN must be defined.")
        String isbn,

        // 不允许为 null 并且值在 1 到 5 之间
        @NotNull(message = "The book quantity must be defined.")
        @Min(value = 1, message = "You must order at least 1 item.")
        @Max(value = 5, message = "You cannot order more than 5 items.")
        Integer quantity
) {
}
