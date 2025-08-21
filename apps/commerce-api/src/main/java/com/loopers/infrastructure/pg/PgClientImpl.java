package com.loopers.infrastructure.pg;

import com.loopers.domain.pg.PgClient;
import com.loopers.domain.pg.PgCommand;
import com.loopers.domain.pg.PgCommonResponse;
import com.loopers.domain.pg.PgInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PgClientImpl implements PgClient {

    private final PgFeignClient pgFeignClient;

    @Override
    public PgCommonResponse<PgInfo.PaymentResult> requestPayment(String userId, PgCommand.PaymentRequest request) {
        return pgFeignClient.requestPayment(userId, request);
    }

    @Override
    public PgCommonResponse<PgInfo.PaymentDetail> getPaymentDetail(String userId, String transactionKey) {
        return pgFeignClient.getPaymentDetail(userId, transactionKey);
    }

    @Override
    public PgCommonResponse<PgInfo.PaymentSearchResult> getPaymentByOrderId(String userId, String orderId) {
        return pgFeignClient.getPaymentByOrderId(userId, orderId);
    }
}
