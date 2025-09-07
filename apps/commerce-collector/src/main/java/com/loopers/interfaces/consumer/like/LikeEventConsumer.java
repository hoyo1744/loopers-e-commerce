package com.loopers.interfaces.consumer.like;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.application.like.LikeEventConsumerHandler;
import com.loopers.application.like.LikedEventCriteria;
import com.loopers.common.kafka.event.EventMessage;
import com.loopers.common.kafka.event.like.LikeOutEvent;
import com.loopers.common.kafka.topic.Topics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikeEventConsumer {

    private final LikeEventConsumerHandler likeEventConsumerHandler;

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = {Topics.LIKE,}, groupId = "like-event-consumer-group")
    public void handleLikeEvent(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            String message = record.value();
            String topic = record.topic();
            Integer partition = record.partition();
            Long offset = record.offset();

            EventMessage<LikeOutEvent.Changed> event = objectMapper.readValue(
                    message,
                    new TypeReference<EventMessage<LikeOutEvent.Changed>>() {}
            );
            LikeOutEvent.Changed payload = event.getPayload();

            likeEventConsumerHandler.handleLikeEvent(
                    LikedEventCriteria.Change.of(
                    event.getPayload().getEventId(),
                    topic,
                    partition,
                    offset,
                    payload.getProductId(),
                    payload.getEventTime(),
                    event.getPayload().isLiked()
                    )
            );

            ack.acknowledge();

        } catch (Exception e) {
            log.error("[Kafka] LIKE 처리 중 예외 발생: {}", e);
            // 향후 DLQ 연동 고려
        }
    }




}
