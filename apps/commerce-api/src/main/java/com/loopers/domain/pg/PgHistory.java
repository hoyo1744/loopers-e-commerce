package com.loopers.domain.pg;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.payment.CardType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "pg_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PgHistory extends BaseEntity {
    @Id
    @Column(name = "pg_history_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private String orderNumber;

    private String cardNo;

    @Enumerated(EnumType.STRING)
    private PgStatus status;

    @Enumerated(EnumType.STRING)
    private CardType cardType;

    private Long amount;



    @Builder
    private PgHistory(Long id, String userId, String orderNumber, String cardNo, PgStatus status, CardType cardType, Long amount) {
        this.id = id;
        this.userId = userId;
        this.orderNumber = orderNumber;
        this.cardNo = cardNo;
        this.status = status;
        this.cardType = cardType;
        this.amount = amount;
    }

    public static PgHistory create(String userId, String orderNumber, String cardNo, PgStatus status, CardType cardType, Long amount) {
        return PgHistory.builder()
                .userId(userId)
                .orderNumber(orderNumber)
                .cardNo(cardNo)
                .status(status)
                .cardType(cardType)
                .amount(amount)
                .build();
    }
}
