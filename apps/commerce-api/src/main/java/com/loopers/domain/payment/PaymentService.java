package com.loopers.domain.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentInfo.Payment create(PaymentCommand.Pay pay) {
        Payment payment = Payment.create(pay.getOrderId(), pay.getAmount(), pay.getOrderNumber(), pay.getPaymentType(), pay.getCardType());

        Payment result = paymentRepository.save(payment);

        return PaymentInfo.Payment.of(result.getId(), result.getAmount(), result.getOrderId(), result.getPaymentStatus().getValue());
    }

    @Transactional
    public PaymentInfo.Payment pay(String orderNumber) {
        Payment payment = paymentRepository.findByOrderNumber(orderNumber);
        payment.pay();

        return PaymentInfo.Payment.of(payment.getId(), payment.getAmount(), payment.getOrderId(), payment.getPaymentStatus().getValue());
    }

    @Transactional
    public void pending(String orderNumber) {
        Payment payment = paymentRepository.findByOrderNumber(orderNumber);
        payment.pending();
    }
}
