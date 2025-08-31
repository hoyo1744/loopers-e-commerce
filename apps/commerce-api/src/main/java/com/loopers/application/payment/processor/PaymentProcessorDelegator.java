package com.loopers.application.payment.processor;

import com.loopers.domain.payment.PaymentType;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentProcessorDelegator {

    private final List<PaymentProcessor> processors;

    @Transactional
    public void process(PaymentType type, PaymentProcessorCriteria.Request request) {
        processors.stream()
                .filter(p -> p.getType().equals(type))
                .findFirst()
                .orElseThrow(() -> new CoreException(ErrorType.BAD_REQUEST, "지원하지 않는 결제 방식입니다."))
                .process(request);
    }

}
