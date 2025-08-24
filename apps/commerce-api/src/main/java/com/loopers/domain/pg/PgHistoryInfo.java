package com.loopers.domain.pg;

import com.loopers.domain.payment.CardType;
import lombok.Builder;
import lombok.Getter;

public class PgHistoryInfo {

    @Getter
    @Builder
    public static class Failed {
        private String userId;
        private String orderNumber;
        private String cardNo;
        private Long amount;
        private CardType cardType;
        private PgStatus status;

        private Failed(String userId, String orderNumber, String cardNo, Long amount, CardType cardType, PgStatus status) {
            this.userId = userId;
            this.orderNumber = orderNumber;
            this.cardNo = cardNo;
            this.amount = amount;
            this.cardType = cardType;
            this.status = status;
        }

        public static Failed from(PgHistory pgHistory) {
            return Failed.builder()
                    .userId(pgHistory.getUserId())
                    .orderNumber(pgHistory.getOrderNumber())
                    .cardNo(pgHistory.getCardNo())
                    .amount(pgHistory.getAmount())
                    .cardType(pgHistory.getCardType())
                    .status(pgHistory.getStatus())
                    .build();
        }

        public static Failed of(String userId, String orderNumber, String cardNo, Long amount, CardType cardType) {
            return Failed.builder()
                    .userId(userId)
                    .orderNumber(orderNumber)
                    .cardNo(cardNo)
                    .amount(amount)
                    .cardType(cardType)
                    .status(PgStatus.FAILED)
                    .build();
        }

    }
}
