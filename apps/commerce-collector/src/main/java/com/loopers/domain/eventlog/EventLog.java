package com.loopers.domain.eventlog;

import com.loopers.common.kafka.event.EventType;
import com.loopers.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "event_log")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventLog extends BaseEntity {

    @Column(name = "event_log_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    private String log;

    private EventLog(String userId, EventType eventType, String log) {
        this.userId = userId;
        this.eventType = eventType;
        this.log = log;
    }

    public static EventLog create(String userId, EventType eventType, String log) {
        return new EventLog(userId, eventType, log);
    }
}
