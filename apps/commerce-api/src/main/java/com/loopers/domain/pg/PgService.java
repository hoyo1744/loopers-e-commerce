package com.loopers.domain.pg;

import com.loopers.support.error.PgServiceRetryException;
import com.loopers.support.error.PgServiceUnavailableException;
import com.loopers.support.error.RetryableBusinessException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PgService {

    private final PgClient pgClient;

    private final PgHistoryRepository pgHistoryRepository;


    @Retry(name = "pgRetry", fallbackMethod = "requestPaymentRetryFallback")
    @CircuitBreaker(name = "pgCircuit", fallbackMethod = "requestPaymentCircuitFallback")
    public PgInfo.PaymentResult requestPayment(String userId, PgCommand.PaymentRequest request) {
        PgCommonResponse<PgInfo.PaymentResult> result = pgClient.requestPayment(userId, request);
        if(!result.getMeta().getResult().equals("SUCCESS")) {
            throw new RetryableBusinessException("PG 응답 실패했습니다.");
        }

        return result.getData();
    }

    public PgInfo.PaymentResult requestPaymentRetryFallback(String userId, PgCommand.PaymentRequest request, Throwable throwable) {
        pgHistoryRepository.save(PgHistory.create(userId, request.getOrderId(), request.getCardNo(),
                PgStatus.FAILED,
                request.getCardType(),
                Long.valueOf(request.getAmount())
                ));
        throw new PgServiceRetryException("PG 서버가 불안정합니다. 잠시후 재시도해주세요.");
    }

    public PgInfo.PaymentResult requestPaymentCircuitFallback(String userId, PgCommand.PaymentRequest request, Throwable throwable) {
        pgHistoryRepository.save(PgHistory.create(userId, request.getOrderId(), request.getCardNo(),
                PgStatus.FAILED,
                request.getCardType(),
                Long.valueOf(request.getAmount())
        ));
        throw new PgServiceUnavailableException("결제 요청에 실패했습니다. 잠시후 재시도해주세요.");
    }

    @Retry(name = "pgRetry", fallbackMethod = "getPaymentDetailRetryFallback")
    @CircuitBreaker(name = "pgCircuit", fallbackMethod = "getPaymentDetailCircuitFallback")
    public PgInfo.PaymentDetail getPaymentDetail(String userId, String transactionId) {
        PgCommonResponse<PgInfo.PaymentDetail> result = pgClient.getPaymentDetail(userId, transactionId);
        if(!result.getMeta().getResult().equals("SUCCESS")) {
            throw new RetryableBusinessException("PG 응답 실패했습니다.");
        }

        return PgInfo.PaymentDetail.of(result.getData().getTransactionKey(),
        result.getData().getOrderId(),
        result.getData().getCardType(),
        result.getData().getCardNo(),
        result.getData().getAmount(),
        result.getData().getStatus(),
                result.getData().getReason()
                );
    }

    public void getPaymentDetailCircuitFallback(String userId, String transactionId, Throwable throwable) {
        throw new PgServiceUnavailableException("PG 결제 상태 조회 실패했습니다. 잠시후 재시도해주세요.", throwable);
    }

    public void getPaymentDetailRetryFallback(String userId, String transactionId, Throwable throwable) {
        throw new PgServiceRetryException("PG 서버가 불안정합니다. 잠시후 재시도해주세요.", throwable);
    }
}
