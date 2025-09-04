package com.loopers.domain.trace;

import com.loopers.common.kafka.event.EventType;

public interface TraceEvent {
    EventType getEvent();

    String getUserId();

    Object getPayload();

}
