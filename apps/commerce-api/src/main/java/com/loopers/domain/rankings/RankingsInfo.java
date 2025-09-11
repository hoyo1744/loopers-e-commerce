package com.loopers.domain.rankings;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class RankingsInfo {

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ProductSummary {
        private Product product;
        private Ranking ranking;

        public static ProductSummary of(Product product, Ranking ranking) {
            return ProductSummary.builder()
                    .product(product)
                    .ranking(ranking)
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Product {
        private Long productId;
        private String productName;
        private Long price;
        private String brandName;
        private Long likeCount;

        public static Product of(Long productId, String productName, Long price, String brandName, Long likeCount) {
            return Product.builder()
                    .productId(productId)
                    .productName(productName)
                    .price(price)
                    .brandName(brandName)
                    .likeCount(likeCount)
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Ranking {
        private Long productId;
        private Long rank;
        private Double score;

        public static Ranking of(Long productId, Long rank, Double score) {
            return Ranking.builder()
                    .productId(productId)
                    .rank(rank)
                    .score(score)
                    .build();
        }
    }
}
