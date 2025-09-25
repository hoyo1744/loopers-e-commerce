package com.loopers.domain.ranking;

public record RankRow(Long productId, Long likeSum, Long saleSum, Long pvSum, Double score) {
}
