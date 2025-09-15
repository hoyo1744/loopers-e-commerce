package com.loopers.interfaces.api.rankings;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class RankingsResponse {

    @Getter
    @Builder
    public static class Rankings {
        private List<Product> rankings;

        private Rankings(List<Product> rankings) {
            this.rankings = rankings;
        }

        public static Rankings of(List<Product> rankings) {
            return Rankings.builder().rankings(rankings).build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Product {
        private String productName;
        private Long price;
        private String brandName;
        private Boolean isLiked;
        private Long likeCount;
        private Long rank;

        public static Product of(String productName, Long price, String brandName, Boolean isLiked, Long likeCount, Long rank) {
            return Product.builder()
                    .productName(productName)
                    .price(price)
                    .brandName(brandName)
                    .isLiked(isLiked)
                    .likeCount(likeCount)
                    .rank(rank)
                    .build();
        }


    }
}
