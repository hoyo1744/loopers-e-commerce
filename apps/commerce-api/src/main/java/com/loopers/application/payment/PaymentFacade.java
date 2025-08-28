package com.loopers.application.payment;

import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.payment.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentFacade {

    private final OrderService orderService;

    private final PaymentService paymentService;

    private final PaymentEventService paymentEventService;


    @Transactional
    public void processPaymentResult(PaymentCriteria.PaymentResult request) {
        if (request.getStatus().equalsIgnoreCase("SUCCESS")) {
            Order order = orderService.getOrder(request.getOrderNumber());

            orderService.complete(order.getOrderNumber());
            // 결제 및 주문 완료 처리
            paymentService.create(PaymentCommand.Create.ofPoint(order.calculateFinalPrice(), order.getId(), order.getOrderNumber()));
            paymentService.pay(PaymentCommand.Pay.of(order.getUserId(), order.getOrderNumber()));

            // 결제 완료 이벤트 발행
            paymentEventService.publishCompleted(PaymentEventCommand.Completed.of(order.getUserCouponId(), order.getOrderNumber()));
        }

    }
}
