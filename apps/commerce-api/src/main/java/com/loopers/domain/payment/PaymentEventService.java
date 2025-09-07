package com.loopers.domain.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentEventService {

    private final PaymentEventPublisher paymentEventPublisher;

    @Transactional
    public void publishRequested(PaymentEventCommand.Requested event) {
        paymentEventPublisher.publish(event.toPaymentRequestedEvent());
    }

    @Transactional
    public void publishCompleted(PaymentEventCommand.Completed event) {
        paymentEventPublisher.publish(event.toPaymentCompletedEvent());
    }

}
