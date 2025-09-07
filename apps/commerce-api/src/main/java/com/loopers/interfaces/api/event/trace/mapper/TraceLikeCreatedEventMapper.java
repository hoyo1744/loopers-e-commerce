package com.loopers.interfaces.api.event.trace.mapper;

import com.loopers.common.kafka.event.EventMessage;
import com.loopers.common.kafka.event.EventType;
import com.loopers.common.kafka.event.trace.TraceLikeOutEvent;
import com.loopers.domain.trace.TraceLikeEvent;
import com.loopers.interfaces.api.event.trace.TraceEventMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TraceLikeCreatedEventMapper implements TraceEventMapper<TraceLikeEvent.LikeCreated> {

    @Override
    public Class<TraceLikeEvent.LikeCreated> supports() {
        return TraceLikeEvent.LikeCreated.class;
    }

    @Override
    public EventMessage<TraceLikeOutEvent.LikeCreated> toMessage(TraceLikeEvent.LikeCreated event) {
        return EventMessage.<TraceLikeOutEvent.LikeCreated>builder()
                .eventType(EventType.TRACE_LIKE_CREATED)
                .version("1.0")
                .payload(TraceLikeOutEvent.LikeCreated.of(event.getUserId(), event.getProductId(), LocalDateTime.now()))
                .build();
    }
}
