package com.loopers.interfaces.consumer.order;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.application.order.OrderEventConsumerHandler;
import com.loopers.application.order.OrderEventCriteria;
import com.loopers.common.kafka.event.EventMessage;
import com.loopers.common.kafka.event.order.OrderOutEvent;
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
public class OrderEventConsumer {

    private final OrderEventConsumerHandler orderEventConsumerHandler;

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = {Topics.ORDER,}, groupId = "order-event-consumer-group")
    public void handleOrderEvent(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            String message = record.value();
            String topic = record.topic();
            Integer partition = record.partition();
            Long offset = record.offset();

            EventMessage<OrderOutEvent.Order> event = objectMapper.readValue(
                    message,
                    new TypeReference<EventMessage<OrderOutEvent.Order>>() {}
            );
            OrderOutEvent.Order payload = event.getPayload();

            orderEventConsumerHandler.handleOrderEvent(
                    OrderEventCriteria.Order.of(
                            event.getPayload().getEventId(),
                            topic,
                            partition,
                            offset,
                            payload.getProductId(),
                            payload.getOrderId(),
                            payload.getOrderNumber(),
                            payload.getQuantity(),
                            payload.getEventTime()
                    )
            );

            ack.acknowledge();

        } catch (Exception e) {
            log.error("[Kafka] ORDER 이벤트 처리 중 예외 발생: {}", e);
        }
    }



}
