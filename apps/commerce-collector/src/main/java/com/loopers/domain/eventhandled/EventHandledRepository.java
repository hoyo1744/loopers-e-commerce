package com.loopers.domain.eventhandled;

public interface EventHandledRepository {
    int markHandled(String eventId,
                    String topic,
                    int partition,
                    long offset);
}
