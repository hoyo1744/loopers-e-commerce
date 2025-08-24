package com.loopers.interfaces.api.payment;

import com.loopers.domain.pg.PgCommonResponse;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.point.PointResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestBody;

public interface PaymentV1ApiSpec {


    @Operation(
            summary = "결제 요청 callback",
            description = "결제 요청에 대한 응답을 비동기로 전달받아 결재 상태를 변경합니다."
    )
    ApiResponse<Void> paymentCallback(PaymentRequest.TransactionInfo transactionInfo);
}
