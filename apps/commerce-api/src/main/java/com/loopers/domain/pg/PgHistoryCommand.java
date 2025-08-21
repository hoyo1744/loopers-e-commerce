package com.loopers.domain.pg;

import lombok.Builder;
import lombok.Getter;

public class PgHistoryCommand {

    @Getter
    @Builder
    public static class Done {
        private Long historyId;
        private String userId;
        private String orderNumber;

        private Done(Long historyId, String userId, String orderNumber) {
            this.historyId = historyId;
            this.userId = userId;
            this.orderNumber = orderNumber;
        }

        public static Done of(Long historyId, String userId, String orderNumber) {
            return Done.builder()
                    .historyId(historyId)
                    .userId(userId)
                    .orderNumber(orderNumber)
                    .build();
        }
    }
}
