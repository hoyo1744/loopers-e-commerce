package com.loopers.interfaces.api.brand;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;

public interface BrandV1ApiSpec {

    @Operation(
            summary = "브랜드 조회",
            description = "브랜드 ID 식별자로 브랜드 정보를 조회합니다."
    )
    ApiResponse<BrandResponse.Brand> getBrand(Long brandId);
}
