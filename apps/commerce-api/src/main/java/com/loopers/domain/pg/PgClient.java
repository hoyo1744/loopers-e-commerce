package com.loopers.domain.pg;

public interface PgClient {

    PgCommonResponse<PgInfo.PaymentResult> requestPayment(String userId, PgCommand.PaymentRequest request);

    PgCommonResponse<PgInfo.PaymentDetail> getPaymentDetail(String userId, String transactionKey);

    PgCommonResponse<PgInfo.PaymentSearchResult> getPaymentByOrderId(String userId, String orderId);
}
