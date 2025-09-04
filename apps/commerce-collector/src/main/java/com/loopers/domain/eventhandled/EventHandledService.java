package com.loopers.domain.eventhandled;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventHandledService {
    private final EventHandledRepository eventHandledRepository;

    @Transactional
    public Boolean processIfNotHandled(String eventId, String topic, int partition, long offset) {
        int inserted = eventHandledRepository.markHandled(eventId, topic, partition, offset);
        return inserted == 1;
    }
}
