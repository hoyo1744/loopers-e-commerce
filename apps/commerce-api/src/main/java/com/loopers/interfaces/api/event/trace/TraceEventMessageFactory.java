package com.loopers.interfaces.api.event.trace;

import com.loopers.common.kafka.event.EventMessage;
import com.loopers.common.kafka.event.trace.TraceOutEvent;
import com.loopers.domain.trace.TraceEvent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class TraceEventMessageFactory {

    private final List<TraceEventMapper<? extends TraceEvent>> mapperList;
    private final Map<Class<? extends TraceEvent>, TraceEventMapper<? extends TraceEvent>> registry = new ConcurrentHashMap<>();

    @PostConstruct
    void init() {
        for (TraceEventMapper<? extends TraceEvent> m : mapperList) {
            registry.put(m.supports(), m);
        }
    }

    @SuppressWarnings("unchecked")
    public EventMessage<? extends TraceOutEvent> create(TraceEvent event) {
        Class<?> eventClass = event.getClass();

        TraceEventMapper<? extends TraceEvent> mapper = registry.get(eventClass);
        if (mapper == null) {
            mapper = registry.entrySet().stream()
                    .filter(e -> e.getKey().isAssignableFrom(eventClass))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElse(null);
        }
        if (mapper == null) {
            throw new IllegalArgumentException("No mapper found for " + eventClass.getName());
        }

        // mapper는 <? extends TraceEvent> 이고, event의 정적 타입은 TraceEvent
        // 안전한 호출을 위해 한 번 캐스팅
        TraceEventMapper<TraceEvent> typed = (TraceEventMapper<TraceEvent>) mapper;
        return typed.toMessage(event); // 반환: EventMessage<? extends TraceEvent>
    }
}
