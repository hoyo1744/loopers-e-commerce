package com.loopers.common.kafka.event;

import java.time.LocalDateTime;

public interface Event {

    String getEventId();

    LocalDateTime getEventTime();

}
