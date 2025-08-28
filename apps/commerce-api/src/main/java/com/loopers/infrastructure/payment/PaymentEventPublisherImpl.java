package com.loopers.infrastructure.payment;

import com.loopers.domain.payment.PaymentEvent;
import com.loopers.domain.payment.PaymentEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PaymentEventPublisherImpl implements PaymentEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(PaymentEvent.Request event) {
        eventPublisher.publishEvent(event);
    }

    @Override
    public void publish(PaymentEvent.Completed event) {
        eventPublisher.publishEvent(event);
    }

}
