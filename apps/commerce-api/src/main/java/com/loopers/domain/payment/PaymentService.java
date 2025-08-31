package com.loopers.domain.payment;

import com.loopers.domain.trace.TraceEventPublisher;
import com.loopers.domain.trace.TracePaymentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private final TraceEventPublisher traceEventPublisher;

    @Transactional
    public PaymentInfo.Payment create(PaymentCommand.Create create) {
        Payment payment = Payment.create(create.getOrderId(), create.getAmount(), create.getOrderNumber(), create.getPaymentType(), create.getCardType());

        Payment result = paymentRepository.save(payment);

        return PaymentInfo.Payment.of(result.getId(), result.getAmount(), result.getOrderId(), result.getPaymentStatus().getValue());
    }

    @Transactional
    public PaymentInfo.Payment pay(PaymentCommand.Pay pay) {
        Payment payment = paymentRepository.findByOrderNumber(pay.getOrderNumber());
        payment.pay();


        traceEventPublisher.publish(TracePaymentEvent.PaymentCompleted.of(
                pay.getUserId(),
                payment.getOrderId(),
                payment.getOrderNumber(),
                payment.getId(),
                payment.getAmount()
        ));

        return PaymentInfo.Payment.of(payment.getId(), payment.getAmount(), payment.getOrderId(), payment.getPaymentStatus().getValue());
    }

    @Transactional
    public void pending(String orderNumber) {
        Payment payment = paymentRepository.findByOrderNumber(orderNumber);
        payment.pending();
    }
}
