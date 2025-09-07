package com.loopers.interfaces.api.event.trace;

import com.loopers.common.kafka.event.EventMessage;
import com.loopers.common.kafka.event.trace.TraceOutEvent;
import com.loopers.domain.trace.TraceEvent;

public interface TraceEventMapper<T extends TraceEvent> {
    Class<T> supports();
    EventMessage<? extends TraceOutEvent> toMessage(T event);
}
