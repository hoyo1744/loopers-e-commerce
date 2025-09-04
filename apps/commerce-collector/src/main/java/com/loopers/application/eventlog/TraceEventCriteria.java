package com.loopers.application.eventlog;

import com.loopers.common.kafka.event.EventType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class TraceEventCriteria {

    @Getter
    @Builder
    public static class Logged {
        private String eventId;
        private String topic;
        private Integer partition;
        private Long offset;
        private LocalDateTime eventTime;

        private String userId;
        private EventType eventType;
        private String log;

        private Logged(String eventId, String topic, Integer partition, Long offset, LocalDateTime eventTime, String userId, EventType eventType, String log) {
            this.eventId = eventId;
            this.topic = topic;
            this.partition = partition;
            this.offset = offset;
            this.eventTime = eventTime;
            this.userId = userId;
            this.eventType = eventType;
            this.log = log;
        }

        public static Logged of(String eventId, String topic, Integer partition, Long offset, LocalDateTime eventTime, String userId, EventType eventType, String log) {
            return Logged.builder()
                    .eventId(eventId)
                    .topic(topic)
                    .partition(partition)
                    .offset(offset)
                    .eventTime(eventTime)
                    .userId(userId)
                    .eventType(eventType)
                    .log(log)
                    .build();
        }
    }
}
