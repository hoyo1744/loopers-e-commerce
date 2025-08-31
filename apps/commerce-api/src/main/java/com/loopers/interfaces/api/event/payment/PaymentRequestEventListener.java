package com.loopers.interfaces.api.event.payment;

import com.loopers.application.payment.PaymentEventCriteria;
import com.loopers.application.payment.PaymentEventFacade;
import com.loopers.domain.payment.PaymentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PaymentRequestEventListener {

    private final PaymentEventFacade paymentEventFacade;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)

    public void handle(PaymentEvent.Request event) {
        paymentEventFacade.handlePaymentRequest(PaymentEventCriteria.PaymentRequest.of(
                event.getUserId(),
                event.getCouponId(),
                PaymentEventCriteria.Payment.of(
                        event.getPayment().getOrderId(),
                        event.getPayment().getCardType(),
                        event.getPayment().getCardNo(),
                        event.getPayment().getAmount(),
                        event.getPayment().getPaymentType()
                )
        ));
    }
}
