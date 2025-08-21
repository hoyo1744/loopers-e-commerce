package com.loopers.domain.pg;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class PgInfo {

    @Getter
    @Builder
    public static class PaymentResult {
        private String transactionKey;
        private String status;

        private PaymentResult(String status, String transactionKey) {
            this.status = status;
            this.transactionKey = transactionKey;
        }

        public static PaymentResult of(String status, String transactionKey) {
            return PaymentResult.builder().status(status).transactionKey(transactionKey).build();
        }
    }

    @Getter
    @Builder
    public static class PaymentDetail {
        private String transactionKey;
        private String orderId;
        private String cardType;
        private String cardNo;
        private String amount;
        private PgStatus status;
        private String reason;

        private PaymentDetail(String transactionKey, String orderId, String cardType, String cardNo, String amount, PgStatus status, String reason) {
            this.transactionKey = transactionKey;
            this.orderId = orderId;
            this.cardType = cardType;
            this.cardNo = cardNo;
            this.amount = amount;
            this.status = status;
            this.reason = reason;
        }

        public static PaymentDetail of(String transactionKey, String orderId, String cardType, String cardNo, String amount, PgStatus status, String reason) {
            return
                    PaymentDetail.builder()
                            .transactionKey(transactionKey)
                            .orderId(orderId)
                            .cardType(cardType)
                            .cardNo(cardNo)
                            .amount(amount)
                            .status(status)
                            .reason(reason)
                            .build();
        }
    }

    @Getter
    @Builder
    public static class PaymentSearchResult {
        private String orderId;
        private List<Transaction> transactions;

        private PaymentSearchResult(String orderId, List<Transaction> transactions) {
            this.orderId = orderId;
            this.transactions = transactions;
        }

        public static PaymentSearchResult of(String orderId, List<Transaction> transactions) {
            return PaymentSearchResult.builder()
                    .orderId(orderId)
                    .transactions(transactions)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class Transaction {
        private String transactionKey;
        private String status;
        private String reason;

        private Transaction(String reason, String status, String transactionKey) {
            this.reason = reason;
            this.status = status;
            this.transactionKey = transactionKey;
        }

        public static Transaction of(String reason, String status, String transactionKey) {
            return Transaction.builder().reason(reason).status(status).transactionKey(transactionKey).build();
        }
    }
}
