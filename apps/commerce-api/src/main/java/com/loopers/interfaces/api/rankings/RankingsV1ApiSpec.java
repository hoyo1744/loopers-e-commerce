package com.loopers.interfaces.api.rankings;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;

import java.time.LocalDate;

public interface RankingsV1ApiSpec {

    @Operation(
            summary = "랭킹 상품 목록 조회",
            description = "랭킹 상품 목록을 조회합니다."
    )
    ApiResponse<RankingsResponse.Rankings> getRankings(
                                                        String userId,
                                                        LocalDate date,
                                                        Long size,
                                                        Long page,
                                                        String period
                                                        );

}
