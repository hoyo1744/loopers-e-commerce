package com.loopers.infrastructure.eventhandled;

import com.loopers.domain.eventhandled.EventHandledRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EventHandledRepositoryImpl implements EventHandledRepository {

    private final EventHandledJpaRepository eventHandledRepository;

    @Override
    public int markHandled(String eventId, String topic, int partition, long offset) {
        return eventHandledRepository.markHandled(eventId, topic, partition, offset);
    }
}
