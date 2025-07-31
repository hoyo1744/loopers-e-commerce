package com.loopers.interfaces.api.like;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;

public interface LikeV1ApiSpec {

    /**
     * 좋아요 상품 추가
     * @param userId 사용자 ID
     * @param productId 상품 ID
     * @return 성공 여부
     */
    @Operation(
            summary = "상품 좋아요",
            description = "사용자의 관심 상품으로 등록합니다."
    )
    ApiResponse<String> likeProduct(String userId, Long productId);

    /**
     * 좋아요 상품 삭제
     * @param userId 사용자 ID
     * @return 성공 여부
     */
    @Operation(
            summary = "상품 좋아요 해제",
            description = "사용자의 관심 상품에서 제거합니다."
    )
    ApiResponse<String> unlikeProduct(String userId, Long productId);

    /**
     * 좋아요 상품 목록 조회
     *
     * @param userId 사용자 ID
     * @return 좋아요 상품 목록
     */
    @Operation(
            summary = "사용자 관심 상품 목록 조회",
            description = "사용자가 관심 상품으로 등록한 상품 목록을 조회합니다."
    )
    ApiResponse<LikeResponse.Products>  getLikedProducts(String userId);
}
