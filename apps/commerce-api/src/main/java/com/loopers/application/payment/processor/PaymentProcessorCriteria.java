package com.loopers.application.payment.processor;

import com.loopers.application.order.OrderCriteria;
import com.loopers.domain.payment.CardType;
import com.loopers.domain.stock.StockCommand;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

public class PaymentProcessorCriteria {

    @Getter
    @Builder
    public static class Request {
        private String userId;
        private Long orderId;
        private Long userCouponId;
        private Long couponId;
        private String orderNumber;
        private CardType cardType;
        private String cardNo;
        private Long amount;
        private List<OrderProduct> orderProducts;

        public static Request of(String userId, Long orderId, Long userCouponId, Long couponId, String orderNumber, CardType cardType, String cardNo, Long amount, List<OrderProduct> orderProducts) {
            return Request.builder()
                    .userId(userId)
                    .orderId(orderId)
                    .userCouponId(userCouponId)
                    .couponId(couponId)
                    .orderNumber(orderNumber)
                    .cardType(cardType)
                    .cardNo(cardNo)
                    .amount(amount)
                    .orderProducts(orderProducts)
                    .build();
        }


        public StockCommand.OrderProducts toStockCommand() {
            List<StockCommand.OrderProduct> collect = orderProducts.stream().map(
                            op -> StockCommand.OrderProduct.of(op.getProductId(), op.getQuantity()))
                    .collect(Collectors.toList());
            return StockCommand.OrderProducts.of(collect);
        }

        public static Request from(OrderCriteria.Order order, Long orderId, Long userCouponId, String orderNumber, Long amount){
            return Request.builder()
                    .userId(order.getUserId())
                    .orderId(orderId)
                    .userCouponId(userCouponId)
                    .couponId(order.getCouponId())
                    .orderNumber(orderNumber)
                    .cardType(order.getCardType())
                    .cardNo(order.getCardNo())
                    .amount(amount)
                    .orderProducts(
                            order.getOrderProducts().stream().map(
                                    op -> OrderProduct.of(op.getProductId(), op.getQuantity())
                            ).collect(Collectors.toList())
                    )
                    .build();

        }

    }

    @Getter
    @Builder
    public static class OrderProduct {
        private Long productId;
        private Long quantity;

        private OrderProduct(Long productId, Long quantity) {
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

}
