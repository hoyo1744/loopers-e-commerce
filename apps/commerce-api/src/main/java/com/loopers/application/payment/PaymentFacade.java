package com.loopers.application.payment;

import com.loopers.domain.order.OrderInfo;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.payment.PaymentService;
import com.loopers.domain.pg.PgCommonResponse;
import com.loopers.domain.stock.StockService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentFacade {

    private final PaymentService paymentService;

    private final OrderService orderService;

    private final StockService stockService;


    @Transactional
    public void processPaymentResult(PaymentCriteria.PaymentResult paymentResult) {
        if (paymentResult.getStatus().equalsIgnoreCase("SUCCESS")) {
            paymentService.pay(paymentResult.getOrderId());
            OrderInfo.OrderProducts orderProducts = orderService.getOrderProducts(paymentResult.getOrderId());
            stockService.decreaseStock(orderProducts.toStockCommandOrderProducts());
            orderService.complete(paymentResult.getOrderId());
        }

    }
}
