package com.loopers.application.payment;

import com.loopers.application.payment.processor.PaymentProcessorCriteria;
import com.loopers.application.payment.processor.PaymentProcessorDelegator;
import com.loopers.common.kafka.event.EventMessage;
import com.loopers.common.kafka.event.EventType;
import com.loopers.common.kafka.event.stock.StockOutEvent;
import com.loopers.common.kafka.topic.Topics;
import com.loopers.domain.external.ExternalDataService;
import com.loopers.domain.external.PaymentResultPayload;
import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderInfo;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.sender.MessageSender;
import com.loopers.domain.stock.StockService;
import com.loopers.domain.usercoupon.UserCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PaymentEventFacade {

    private final PaymentProcessorDelegator paymentProcessorDelegator;

    private final OrderService orderService;

    private final StockService stockService;

    private final UserCouponService userCouponService;

    private final ExternalDataService externalDataService;

    private final MessageSender messageSender;

    @Transactional
    public void handlePaymentRequest(PaymentEventCriteria.PaymentRequest event) {
        Order order = orderService.getOrder(event.getPayment().getOrderNumber());

        paymentProcessorDelegator.process(event.getPayment().getPaymentType(),
                PaymentProcessorCriteria.Request.of(
                        event.getUserId(),
                        order.getId(),
                        order.getUserCouponId(),
                        event.getCouponId(),
                        order.getOrderNumber(),
                        event.getPayment().getCardType(),
                        event.getPayment().getCardNo(),
                        order.calculateFinalPrice(),
                        order.getOrderProducts().stream().map( op -> PaymentProcessorCriteria.OrderProduct.of(op.getProductId(), op.getQuantity()))
                                .toList()
                ));

    }

    @Transactional
    public void handlePaymentCompleted(PaymentEventCriteria.PaymentCompleted event) {
        OrderInfo.OrderProducts orderProducts = orderService.getOrderProducts(event.getOrderNumber());
        stockService.decreaseStock(orderProducts.toStockCommandOrderProducts());

        orderProducts.getOrderProductDtos().forEach(op -> {
            messageSender.send(Topics.STOCK, op.getProductId().toString(),
                    EventMessage.<StockOutEvent.Adjusted>builder()
                            .eventType(EventType.STOCK_ADJUSTED)
                            .version("v1")
                            .payload(StockOutEvent.Adjusted.of(
                                    op.getProductId(),
                                    op.getQuantity(),
                                            LocalDateTime.now(),
                                    true
                                    ))
                            .build()
            );
        });


        if (!(event.getUserCouponId() == null)) {
            userCouponService.useCoupon(event.getUserCouponId());
        }
        externalDataService.send("PAYMENT", PaymentResultPayload.of(event.getUserCouponId(), event.getOrderNumber()));
    }


}
