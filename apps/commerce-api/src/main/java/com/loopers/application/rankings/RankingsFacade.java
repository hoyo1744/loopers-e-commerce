package com.loopers.application.rankings;

import com.loopers.domain.like.LikeService;
import com.loopers.domain.rankings.RankingsInfo;
import com.loopers.domain.rankings.RankingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RankingsFacade {

    private final RankingsService rankingsService;

    private final LikeService likeService;

    @Transactional
    public List<RankingsResult.Rankings> getRankings(RankingsCriteria.PageInfo page) {
        List<RankingsInfo.ProductSummary> productByRank = rankingsService.getProductByRank(page.toPageInfo());

        List<Long> likedProductIdsByUserId = likeService.getLikedProductIdsByUserId(page.getUserId());

        return productByRank.stream()
                .map(p -> RankingsResult.Rankings.of(
                                p.getProduct().getProductName(),
                                p.getProduct().getBrandName(),
                                p.getProduct().getPrice(),
                                p.getRanking().getRank(),
                                p.getRanking().getScore(),
                                likedProductIdsByUserId.contains(p.getProduct().getProductId()),
                                p.getProduct().getLikeCount()

                        )
                ).toList();
    }

}
