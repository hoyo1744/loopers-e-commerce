package com.loopers.interfaces.api.rankings;

import com.loopers.application.rankings.RankingsCriteria;
import com.loopers.application.rankings.RankingsFacade;
import com.loopers.application.rankings.RankingsResult;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rankings")
@RequiredArgsConstructor
public class RankingsV1Controller implements RankingsV1ApiSpec {

    private final RankingsFacade rankingFacade;

    @Override
    @GetMapping
    public ApiResponse<RankingsResponse.Rankings> getRankings(@RequestHeader(value = "X-USER-ID", required = false) String userId,
                                                              @RequestParam LocalDate date,
                                                              @RequestParam(defaultValue = "20") Long size,
                                                              @RequestParam(defaultValue = "1") Long page,
                                                              @RequestParam String period
                                                              ) {

        List<RankingsResult.Rankings> rankings = rankingFacade.getRankings(RankingsCriteria.PageInfo.of(userId, date, page, size, "all", period));

        return ApiResponse.success(
                RankingsResponse.Rankings.of(
                        rankings.stream().map(ranking ->
                                RankingsResponse.Product.of(
                                        ranking.getProductName(),
                                        ranking.getPrice(),
                                        ranking.getBrandName(),
                                        ranking.getIsLiked(),
                                        ranking.getLikeCount(),
                                        ranking.getRank()
                                )
                        ).toList()
                )
        );
    }
}
