package com.loopers.interfaces.api.event.trace.mapper;

import com.loopers.common.kafka.event.EventMessage;
import com.loopers.common.kafka.event.EventType;

import com.loopers.common.kafka.event.trace.TraceOrderOutEvent;
import com.loopers.domain.trace.TraceOrderEvent;
import com.loopers.interfaces.api.event.trace.TraceEventMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TraceOrderCompletedEventMapper implements TraceEventMapper<TraceOrderEvent.OrderCompleted> {

    @Override
    public Class<TraceOrderEvent.OrderCompleted> supports() {
        return TraceOrderEvent.OrderCompleted.class;
    }

    @Override
    public EventMessage<TraceOrderOutEvent.OrderCompleted> toMessage(TraceOrderEvent.OrderCompleted event) {
        return EventMessage.<TraceOrderOutEvent.OrderCompleted>builder()
                .eventType(EventType.TRACE_ORDER_COMPLETED)
                .version("1.0")
                .payload(TraceOrderOutEvent.OrderCompleted.of(event.getUserId(), event.getOrderId(), event.getOrderNumber(), event.getAmount(), LocalDateTime.now()))
                .build();
    }
}
