package com.loopers.application.payment.processor;

import com.loopers.domain.payment.PaymentType;

public interface PaymentProcessor {
    PaymentType getType();

    void process(PaymentProcessorCriteria.Request request);

}
