package com.loopers.interfaces.api.order;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;

public interface OrderV1ApiSpec {

    @Operation(
            summary = "주문",
            description = "주문 및 결제를 진행합니다."
    )
    ApiResponse<Void> order(String userId, OrderRequest.Order order);

    @Operation(
            summary = "주문 목록 조회",
            description = "사용자의 주문 목록을 조회합니다."
    )
    ApiResponse<OrderResponse.Orders> getOrders(String userId);

    @Operation(
            summary = "주문 상세 조회",
            description = "사용자 주문을 상세 조회합니다."
    )
    ApiResponse<OrderResponse.Order> getOrder(String userId, Long orderId);
}
