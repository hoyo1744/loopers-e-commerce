package com.loopers.interfaces.api.payment;

import com.loopers.application.payment.PaymentCriteria;
import com.loopers.application.payment.PaymentFacade;
import com.loopers.domain.pg.PgCommonResponse;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController implements PaymentV1ApiSpec{

    private final PaymentFacade paymentFacade;

    @PostMapping("/callback")
    public ApiResponse<Void> paymentCallback(@RequestBody PaymentRequest.TransactionInfo transactionInfo) {
        paymentFacade.processPaymentResult(transactionInfo.toPaymentResult());
        return ApiResponse.success();
    }
}
