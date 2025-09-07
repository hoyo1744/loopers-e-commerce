package com.loopers.interfaces.consumer.stock;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.application.stock.StockEventConsumerHandler;
import com.loopers.application.stock.StockEventCriteria;
import com.loopers.common.kafka.event.EventMessage;
import com.loopers.common.kafka.event.stock.StockOutEvent;
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
public class StockEventConsumer {

    private final StockEventConsumerHandler stockEventConsumerHandler;

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = {Topics.STOCK}, groupId = "stock-event-consumer-group")
    public void handleStockEvent(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            String message = record.value();
            String topic = record.topic();
            Integer partition = record.partition();
            Long offset = record.offset();

            EventMessage<StockOutEvent.Adjusted> event = objectMapper.readValue(
                    message,
                    new TypeReference<EventMessage<StockOutEvent.Adjusted>>() {}
            );
            StockOutEvent.Adjusted payload = event.getPayload();

            stockEventConsumerHandler.handleStockEvent(
                    StockEventCriteria.Adjusted.of(
                            payload.getEventId(),
                            topic,
                            partition,
                            offset,
                            payload.getEventTime(),
                            payload.getProductId(),
                            payload.getQuantity(),
                            payload.getIsSales()
                    )
            );

            ack.acknowledge();

        } catch (Exception e) {
            log.error("[Kafka] LIKE 처리 중 예외 발생: {}", e);
            // 향후 DLQ 연동 고려
        }
    }


}
