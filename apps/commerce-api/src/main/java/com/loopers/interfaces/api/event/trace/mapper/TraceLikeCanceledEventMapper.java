package com.loopers.interfaces.api.event.trace.mapper;

import com.loopers.common.kafka.event.EventMessage;
import com.loopers.common.kafka.event.EventType;
import com.loopers.common.kafka.event.trace.TraceLikeOutEvent;
import com.loopers.domain.trace.TraceLikeEvent;
import com.loopers.interfaces.api.event.trace.TraceEventMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TraceLikeCanceledEventMapper implements TraceEventMapper<TraceLikeEvent.LikeCanceled> {

    @Override
    public Class<TraceLikeEvent.LikeCanceled> supports() {
        return TraceLikeEvent.LikeCanceled.class;
    }

    @Override
    public EventMessage<TraceLikeOutEvent.LikeCanceled> toMessage(TraceLikeEvent.LikeCanceled event) {
        return EventMessage.<TraceLikeOutEvent.LikeCanceled>builder()
                .eventType(EventType.TRACE_LIKE_CANCELED)
                .version("1.0")
                .payload(TraceLikeOutEvent.LikeCanceled.of(event.getUserId(), event.getProductId(), LocalDateTime.now()))
                .build();
    }
}
