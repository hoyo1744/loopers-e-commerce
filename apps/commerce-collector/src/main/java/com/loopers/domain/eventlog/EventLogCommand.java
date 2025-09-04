package com.loopers.domain.eventlog;

import com.loopers.common.kafka.event.EventType;
import lombok.Builder;
import lombok.Getter;

public class EventLogCommand {

    @Getter
    @Builder
    public static class Event {
        private String userId;
        private EventType eventType;
        private String log;

        private Event(String userId, EventType eventType, String log) {
            this.userId = userId;
            this.eventType = eventType;
            this.log = log;
        }

        public static Event of(String userId, EventType eventType, String log) {
            return new Event(userId, eventType, log);
        }
    }

}
