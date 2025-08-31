package com.loopers.interfaces.api.event.trace;

import com.loopers.domain.trace.TraceEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TraceEventListener {

    @EventListener
    public void logTrace(TraceEvent event) {
        log.info("[TRACE] event={}, userId={}, payload={}", event.getEvent(), event.getUserId(), event.getPayload());
    }
}
