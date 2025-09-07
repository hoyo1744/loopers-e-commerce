package com.loopers.infrastructure.sender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.common.kafka.event.EventMessage;
import com.loopers.domain.sender.MessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaProducer implements MessageSender {

    private final KafkaTemplate<Object, Object> kafkaTemplate;

    private final ObjectMapper objectMapper;

    public <T> void send(String topic, String key, EventMessage<T> message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(topic, key, json);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Kafka 메시지 직렬화 실패", e);
        }
    }
}
