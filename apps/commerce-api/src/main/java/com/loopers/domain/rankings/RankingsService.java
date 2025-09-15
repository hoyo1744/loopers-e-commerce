package com.loopers.domain.rankings;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RankingsService {

    private final RankingsRepository rankingsRepository;

    public List<RankingsInfo.ProductSummary> getProductByRank(RankingsCommand.PageInfo page) {
        List<RankingsInfo.Ranking> productsByRank = rankingsRepository.getProductsByRank(page);

        List<RankingsInfo.Product> productsByIds = rankingsRepository.getProductsByIds(productsByRank.stream().map(RankingsInfo.Ranking::getProductId).toList());

        return productsByRank.stream()
                .map(ranking -> {
                    RankingsInfo.Product product = productsByIds.stream()
                            .filter(p -> p.getProductId().equals(ranking.getProductId()))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("Product not found: " + ranking.getProductId()));
                    return RankingsInfo.ProductSummary.of(
                            product,
                            ranking
                    );
                })
                .toList();
    }

    public Optional<RankingsInfo.Ranking> getRanking(Long productId) {
         return rankingsRepository.getProductRanking(productId);
    }
}
