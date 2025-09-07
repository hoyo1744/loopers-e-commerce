package com.loopers.common.kafka.event.stock;

import com.loopers.common.kafka.event.Event;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

public class StockOutEvent {

    @Getter
    @Builder
    public static class Adjusted implements Event {
        private String eventId;
        private Long productId;
        private Long quantity;
        private LocalDateTime eventTime;
        private Boolean isSales;

        public static Adjusted of(Long productId, Long quantity, LocalDateTime eventTime, Boolean isSales) {
            return Adjusted
                    .builder()
                    .eventId(UUID.randomUUID().toString())
                    .productId(productId)
                    .quantity(quantity)
                    .eventTime(eventTime)
                    .isSales(isSales)
                    .build();
        }
    }
}
