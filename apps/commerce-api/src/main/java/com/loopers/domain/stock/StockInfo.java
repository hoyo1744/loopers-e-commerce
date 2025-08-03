package com.loopers.domain.stock;

import lombok.Builder;
import lombok.Getter;

public class StockInfo {

    @Getter
    @Builder
    public static class Stock {
        private Long productId;
        private Long quantity;

        private Stock(Long productId, Long quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public static Stock of(Long productId, Long quantity) {
            return Stock.builder().productId(productId).quantity(quantity).build();
        }
    }

}
