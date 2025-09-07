package com.loopers.domain.sender;

import com.loopers.common.kafka.event.EventMessage;

public interface MessageSender {
    <T> void send(String topic, String key, EventMessage<T> message);

}
