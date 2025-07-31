package com.loopers.interfaces.api.product;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;

public interface ProductV1ApiSpec {

    @Operation(
            summary = "상품 목록 조회",
            description = "상품 목록을 조회합니다."
    )
    ApiResponse<ProductResponse.Products> getProducts(String userId,
                                                        Long brandId,
                                                        String sort,
                                                        Long page,
                                                        Long size);


    @Operation(
            summary = "상품 상세 조회",
            description = "상품 ID 식별자로 상품의 상세 정보를 조회합니다."
    )
    ApiResponse<ProductResponse.ProductDetail> getProductDetail(String userId, Long productId);

}
