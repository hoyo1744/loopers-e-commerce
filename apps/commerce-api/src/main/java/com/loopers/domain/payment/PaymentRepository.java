package com.loopers.domain.payment;

public interface PaymentRepository {

    Payment findById(Long id);

    Payment findByOrderNumber(String orderNumber);

    Payment save(Payment payment);
}
