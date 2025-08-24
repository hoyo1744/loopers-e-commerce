package com.loopers.scheduler;

import com.loopers.domain.order.OrderService;
import com.loopers.domain.payment.PaymentService;
import com.loopers.domain.pg.*;
import com.loopers.support.error.PgServiceRetryException;
import com.loopers.support.error.PgServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PgRetryScheduler {

    private final PgHistoryService historyService;

    private final OrderService orderService;

    private final PgService pgService;

    private final PaymentService paymentService;


    @Scheduled(cron = "0 0/5 * * * *")
    public void retryFailedPgRequests() {
        List<PgHistoryInfo.Failed> paymentFailedList = historyService.getPaymentFailedList();

        List<String> failedOrderNumbers = new ArrayList<>();

        for (PgHistoryInfo.Failed failed : paymentFailedList) {
            try {

                PgCommand.PaymentRequest retryRequest = PgCommand.PaymentRequest.of(
                        failed.getOrderNumber(),
                        failed.getCardType(),
                        failed.getCardNo(),
                        String.valueOf(failed.getAmount()),
                        PgConstant.PAYMENT_REQUEST_CALLBACK
                );

                pgService.requestPayment(failed.getUserId(), retryRequest);
                paymentService.pay(failed.getOrderNumber());
                orderService.complete(failed.getOrderNumber());
                historyService.complete(failed.getOrderNumber());
            } catch (PgServiceRetryException | PgServiceUnavailableException ignored) {
                failedOrderNumbers.add(failed.getOrderNumber());
            }
        }

        if(!failedOrderNumbers.isEmpty()) {
            historyService.touch(failedOrderNumbers);
        }
    }
}
