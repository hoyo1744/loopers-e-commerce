package com.loopers.interfaces.api.order;

import com.loopers.application.order.OrderCriteria;
import com.loopers.domain.payment.CardType;
import com.loopers.domain.payment.PaymentType;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

public class OrderRequest {


    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    public static class Order {
        private List<OrderProduct> orderProducts;
        private Long couponId;
        private PaymentTypeDto paymentTypeDto;
        private CardTypeDto cardTypeDto;
        private String cardNo;

        public Order(List<OrderProduct> orderProducts, Long couponId, PaymentTypeDto paymentTypeDto, CardTypeDto cardTypeDto, String cardNo) {
            this.orderProducts = orderProducts;
            this.couponId = couponId;
            this.paymentTypeDto = paymentTypeDto;
            this.cardTypeDto = cardTypeDto;
            this.cardNo = cardNo;
        }

        public static Order of(List<OrderProduct> orderItems, Long couponId, PaymentTypeDto paymentTypeDto, CardTypeDto cardTypeDto, String cardNo) {
            return Order.builder()
                    .orderProducts(orderItems)
                    .couponId(couponId)
                    .paymentTypeDto(paymentTypeDto)
                    .cardTypeDto(cardTypeDto)
                    .cardNo(cardNo)
                    .build();
        }

        public OrderCriteria.Order toOrderCriteria(String userId) {
            return OrderCriteria.Order.of(userId, this.getOrderProducts().stream()
                    .map(op -> OrderCriteria.OrderProduct.of(op.getProductId(), op.getQuantity()))
                    .collect(Collectors.toList()), this.getCouponId(), this.getPaymentTypeDto().toPaymentType(),
                    this.getPaymentTypeDto() == PaymentTypeDto.CARD ? this.getCardTypeDto().toCardType() : null, cardNo);
        }
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    public static class OrderProduct {
        private Long productId;
        private Long quantity;

        public OrderProduct(Long productId, Long quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public static OrderProduct of(Long productId, Long quantity) {
            return OrderProduct.builder()
                    .productId(productId)
                    .quantity(quantity)
                    .build();
        }
    }


    @Getter
    @RequiredArgsConstructor
    public static enum PaymentTypeDto {

        POINT("POINT"),
        CARD("CARD"),
        ;

        private final String type;

        public PaymentType toPaymentType() {
            return PaymentType.valueOf(this.name());
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static enum CardTypeDto {
        SAMSUNG("SAMSUNG"),
        KB("KB"),
        HYUNDAI("HYUNDAI"),
        ;

        private final String name;

        public CardType toCardType() {
            return CardType.valueOf(this.name());
        }
    }
}
