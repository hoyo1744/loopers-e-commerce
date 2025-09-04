package com.loopers.common.kafka.event.trace;

import com.loopers.common.kafka.event.Event;
import com.loopers.common.kafka.event.EventType;

public interface TraceOutEvent extends Event {

    String getTopic();

    String getUserId();

    String toLog();

    EventType getEventType();
}
