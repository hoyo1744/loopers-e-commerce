package com.loopers.interfaces.api.event.external;

import com.loopers.common.kafka.event.EventMessage;
import com.loopers.common.kafka.event.EventType;
import com.loopers.common.kafka.event.order.OrderOutEvent;
import com.loopers.common.kafka.topic.Topics;
import com.loopers.domain.external.ExternalDataService;
import com.loopers.domain.external.OrderResultPayload;
import com.loopers.domain.order.OrderEvent;
import com.loopers.domain.sender.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ExternalDataPlatformListener {

    private final ExternalDataService externalDataService;

    private final MessageSender messageSender;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(OrderEvent.Completed event) {
        externalDataService.send("ORDER", OrderResultPayload.of(event.getOrderId(), event.getOrderNumber(), event.getAmount()));

        event.getOrderProducts().stream().forEach(
                orderProduct -> {
                    messageSender.send(Topics.ORDER,
                            event.getUserId().toString(),
                            EventMessage.<OrderOutEvent.Order>builder()
                                    .eventType(EventType.ORDER_COMPLETED)
                                    .version("v1")
                                    .payload(OrderOutEvent.Order.of(
                                            event.getUserId(),
                                            orderProduct.getProductId(),
                                            event.getOrderId(),
                                            event.getOrderNumber(),
                                            orderProduct.getQuantity(),
                                            LocalDateTime.now()))
                                    .build()
                    );
                }
        );
    }

}
