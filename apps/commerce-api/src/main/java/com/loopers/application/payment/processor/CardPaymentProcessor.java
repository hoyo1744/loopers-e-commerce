package com.loopers.application.payment.processor;

import com.loopers.domain.payment.PaymentType;
import com.loopers.domain.pg.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CardPaymentProcessor implements PaymentProcessor{

    private final PgService pgService;

    @Override
    public PaymentType getType() {
        return PaymentType.CARD;
    }

    @Override
    @Transactional
    public void process(PaymentProcessorCriteria.Request request) {
        pgService.requestPayment(request.getUserId(), PgCommand.PaymentRequest.of(
                request.getOrderNumber(),
                request.getCardType(),
                request.getCardNo(),
                String.valueOf(request.getAmount()),
                PgConstant.PAYMENT_REQUEST_CALLBACK
        ));

    }
}
