package com.loopers.interfaces.api.point;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;

public interface PointV1ApiSpec {

    @Operation(
            summary = "포인트 조회",
            description = "회원 ID 식별자로 보유 포인트를 조회합니다."
    )
    ApiResponse<PointResponse.Point> getPoints(
            @Schema(name = "id", description = "User ID", example = "hoyong.eom" )
            String userId);


    @Operation(
            summary = "포인트 충전",
            description = "포인트 충전 금액을 입력받아 충전합니다."
    )
    ApiResponse<PointResponse.ChargedPoint> charge(String userId, Long amount);
}
