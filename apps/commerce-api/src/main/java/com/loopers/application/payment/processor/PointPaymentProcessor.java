package com.loopers.application.payment.processor;

import com.loopers.domain.order.OrderService;
import com.loopers.domain.payment.*;
import com.loopers.domain.point.PointCommand;
import com.loopers.domain.point.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PointPaymentProcessor implements PaymentProcessor{

    private final PaymentService paymentService;

    private final PointService pointService;

    private final OrderService orderService;

    @Override
    public PaymentType getType() {
        return PaymentType.POINT;
    }

    @Override
    @Transactional
    public void process(PaymentProcessorCriteria.Request request) {
        // 포인트 감소
        pointService.deductPoint(PointCommand.Use.of(request.getUserId(), request.getAmount()));

        // 결제 및 주문 완료 처리
        paymentService.create(PaymentCommand.Create.ofPoint(request.getAmount(), request.getOrderId(), request.getOrderNumber()));
        paymentService.pay(PaymentCommand.Pay.of(request.getUserId(), request.getOrderNumber()));
        orderService.complete(request.getOrderNumber());

    }
}
