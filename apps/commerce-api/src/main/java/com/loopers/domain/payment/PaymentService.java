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
    public PaymentInfo.Payment pay(PaymentCommand.Pay pay) {
        Payment payment = Payment.create(pay.getOrderId(), pay.getAmount());
        payment.pay();

        Payment result = paymentRepository.save(payment);

        return PaymentInfo.Payment.of(result.getId(), result.getAmount(), result.getOrderId(), result.getPaymentStatus().getValue());
    }

}
