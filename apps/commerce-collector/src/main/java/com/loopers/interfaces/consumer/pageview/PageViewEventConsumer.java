package com.loopers.interfaces.consumer.pageview;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.application.pageview.PageViewEventConsumerHandler;
import com.loopers.application.pageview.PageViewEventCriteria;
import com.loopers.common.kafka.event.EventMessage;
import com.loopers.common.kafka.event.pageview.PageViewOutEvent;
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
public class PageViewEventConsumer {
    private final PageViewEventConsumerHandler pageViewEventConsumerHandler;

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = {Topics.PAGE_VIEW,}, groupId = "page-view-event-consumer-group")
    public void handlePageViewEvent(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            String message = record.value();
            String topic = record.topic();
            Integer partition = record.partition();
            Long offset = record.offset();

            EventMessage<PageViewOutEvent.Viewed> event = objectMapper.readValue(
                    message,
                    new TypeReference<EventMessage<PageViewOutEvent.Viewed>>() {}
            );
            PageViewOutEvent.Viewed payload = event.getPayload();

            pageViewEventConsumerHandler.handlePageViewEvent(
                    PageViewEventCriteria.View.of(
                            event.getPayload().getEventId(),
                            topic,
                            partition,
                            offset,
                            payload.getProductId(),
                            payload.getEventTime()
                    )
            );

            ack.acknowledge();

        } catch (Exception e) {
            log.error("[Kafka] PageView 처리 중 예외 발생: {}", e);
        }
    }
}
