package com.loopers.interfaces.api.event.trace.mapper;

import com.loopers.common.kafka.event.EventMessage;
import com.loopers.common.kafka.event.EventType;

import com.loopers.common.kafka.event.trace.TracePaymentOutEvent;
import com.loopers.domain.trace.TracePaymentEvent;
import com.loopers.interfaces.api.event.trace.TraceEventMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TracePaymentCompletedEventMapper implements TraceEventMapper<TracePaymentEvent.PaymentCompleted> {

    @Override
    public Class<TracePaymentEvent.PaymentCompleted> supports() {
        return TracePaymentEvent.PaymentCompleted.class;
    }

    @Override
    public EventMessage<TracePaymentOutEvent.PaymentCompleted> toMessage(TracePaymentEvent.PaymentCompleted event) {
        return EventMessage.<TracePaymentOutEvent.PaymentCompleted>builder()
                .eventType(EventType.TRACE_PAYMENT_COMPLETED)
                .version("1.0")
                .payload(TracePaymentOutEvent.PaymentCompleted.of(event.getUserId(), event.getOrderId(), event.getOrderNumber(), event.getPaymentId(), event.getAmount(), LocalDateTime.now()))
                .build();
    }
}
