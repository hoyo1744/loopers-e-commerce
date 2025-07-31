package com.loopers.domain.payment;

public interface PaymentRepository {

    Payment findById(Long id);

    Payment save(Payment payment);
}
