package com.loopers.infrastructure.pg;

import com.loopers.config.PgFeignClientConfig;
import com.loopers.domain.pg.PgCommand;
import com.loopers.domain.pg.PgCommonResponse;
import com.loopers.domain.pg.PgInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "pgClient", url = "${pg.simulator.url}", configuration = PgFeignClientConfig.class)
public interface PgFeignClient {

    @PostMapping("/api/v1/payments")
    PgCommonResponse<PgInfo.PaymentResult> requestPayment(
            @RequestHeader("X-USER-ID") String userId,
            @RequestBody PgCommand.PaymentRequest request
    );

    @GetMapping("/api/v1/payments/{transactionKey}")
    PgCommonResponse<PgInfo.PaymentDetail> getPaymentDetail(
            @RequestHeader("X-USER-ID") String userId,
            @PathVariable("transactionKey") String transactionKey
    );

    @GetMapping("/api/v1/payments")
    PgCommonResponse<PgInfo.PaymentSearchResult> getPaymentByOrderId(
            @RequestHeader("X-USER-ID") String userId,
            @RequestParam("orderId") String orderId
    );
}
