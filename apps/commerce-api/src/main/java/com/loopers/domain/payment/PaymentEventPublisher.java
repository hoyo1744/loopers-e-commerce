package com.loopers.domain.payment;

public interface PaymentEventPublisher {

    void publish(PaymentEvent.Request event);

    void publish(PaymentEvent.Completed event);
}
