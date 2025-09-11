package com.loopers.application.rankings;

import lombok.*;

public class RankingsResult {

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Rankings {
        private String productName;
        private String brandName;
        private Long price;
        private Long rank;
        private Double score;
        private Boolean isLiked;
        private Long likeCount;

        public static Rankings of(String productName, String brandName, Long price, Long rank, Double score, Boolean isLiked, Long likeCount) {
            return Rankings.builder()
                    .productName(productName)
                    .brandName(brandName)
                    .price(price)
                    .rank(rank)
                    .score(score)
                    .isLiked(isLiked)
                    .likeCount(likeCount)
                    .build();
        }
    }
}
