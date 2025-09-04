package com.loopers.interfaces.consumer.eventlog;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.application.eventlog.TraceEventConsumerHandler;
import com.loopers.application.eventlog.TraceEventCriteria;
import com.loopers.common.kafka.event.EventMessage;
import com.loopers.common.kafka.event.trace.TraceLikeOutEvent;
import com.loopers.common.kafka.event.trace.TraceOrderOutEvent;
import com.loopers.common.kafka.event.trace.TraceOutEvent;
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
public class TraceEventConsumer {


    private final TraceEventConsumerHandler traceEventConsumerHandler;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = {Topics.TRACE}, groupId = "trace-consumer-group")
    public void handleLikeEvent(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            String message = record.value();
            String topic = record.topic();
            Integer partition = record.partition();
            Long offset = record.offset();

            EventMessage<JsonNode> msg =
                    objectMapper.readValue(message, new TypeReference<>() {});

            // TODO : eventType에 따른 분기 처리 개선 방안 고민
            TraceOutEvent event = switch (msg.getEventType()) {
                case TRACE_ORDER_COMPLETED -> objectMapper.convertValue(
                        msg.getPayload(), TraceOrderOutEvent.OrderCompleted.class);
                case TRACE_LIKE_CREATED -> objectMapper.convertValue(
                        msg.getPayload(), TraceLikeOutEvent.LikeCreated.class);
                case TRACE_LIKE_CANCELED -> objectMapper.convertValue(
                        msg.getPayload(), TraceLikeOutEvent.LikeCanceled.class);
                default -> throw new IllegalArgumentException("Unknown eventType: " + msg.getEventType());
            };

            traceEventConsumerHandler.handleTraceEvent(
                    TraceEventCriteria.Logged.of(
                            event.getEventId(),
                            topic,
                            partition,
                            offset,
                            event.getEventTime(),
                            event.getUserId(),
                            event.getEventType(),
                            event.toLog()
                    )
            );

            ack.acknowledge();

        } catch (Exception e) {
            log.error("[Kafka] TRACE 처리 중 예외 발생: {}",  e);
            // 향후 DLQ 연동 고려
        }
    }
}
