package com.loopers.application.pg;


import com.loopers.domain.pg.PgService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PgFacade {
    private final PgService pgService;

    public void requestPayment(PgCriteria.PaymentEvent paymentEvent) {
        pgService.requestPayment(paymentEvent.getUserId(), paymentEvent.getPayment().toPgCommand());
    }
}
