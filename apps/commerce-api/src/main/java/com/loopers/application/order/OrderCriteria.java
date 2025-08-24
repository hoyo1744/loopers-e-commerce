package com.loopers.application.order;

import com.loopers.domain.payment.CardType;
import com.loopers.domain.payment.PaymentType;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.stock.StockCommand;
import com.loopers.domain.usercoupon.UserCouponCommand;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

public class OrderCriteria {

    @Getter
    @Builder
    public static class Order {
        String userId;
        List<OrderProduct> orderProducts;
        Long couponId;
        PaymentType paymentType;
        CardType cardType;
        String cardNo;

        private Order(String userId, List<OrderProduct> orderProducts, Long couponId, PaymentType paymentType, CardType cardType, String cardNo) {
            this.userId = userId;
            this.orderProducts = orderProducts;
            this.couponId = couponId;
            this.paymentType = paymentType;
            this.cardType = cardType;
            this.cardNo = cardNo;
        }

        public static Order ofCard(String userId, List<OrderProduct> orderProducts, Long couponId, CardType cardType, String cardNo) {
            return Order.builder()
                    .userId(userId)
                    .orderProducts(orderProducts)
                    .couponId(couponId)
                    .paymentType(PaymentType.CARD)
                    .cardType(cardType)
                    .cardNo(cardNo)
                    .build();
        }

        public static Order ofPoint(String userId, List<OrderProduct> orderProducts, Long couponId) {
            return Order.builder()
                    .userId(userId)
                    .orderProducts(orderProducts)
                    .couponId(couponId)
                    .paymentType(PaymentType.POINT)
                    .build();
        }

        public static Order of(String userId, List<OrderProduct> orderProducts, Long couponId, PaymentType paymentType, CardType cardType, String cardNo) {
            return Order.builder()
                    .userId(userId)
                    .orderProducts(orderProducts)
                    .couponId(couponId)
                    .paymentType(paymentType)
                    .cardType(cardType)
                    .cardNo(cardNo)
                    .build();

        }

        public ProductCommand.OrderProducts toProductCommand() {
            List<ProductCommand.OrderProduct> collect = orderProducts.stream().map(
                            op -> ProductCommand.OrderProduct.of(op.getProductId(), op.getQuantity()))
                    .collect(Collectors.toList());
            return ProductCommand.OrderProducts.of(collect);
        }

        public StockCommand.OrderProducts toStockCommand() {
            List<StockCommand.OrderProduct> collect = orderProducts.stream().map(
                            op -> StockCommand.OrderProduct.of(op.getProductId(), op.getQuantity()))
                    .collect(Collectors.toList());
            return StockCommand.OrderProducts.of(collect);
        }

        public UserCouponCommand.UserCoupon toUserCouponCommand() {
            return UserCouponCommand.UserCoupon.of(userId, couponId);
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

    @Getter
    @Builder
    public static class OrderDetail {
        private String userId;
        private Long orderId;

        private OrderDetail(String userId, Long orderId) {
            this.userId = userId;
            this.orderId = orderId;
        }

        public static OrderDetail of(String userId, Long orderId) {
            return OrderDetail.builder()
                    .userId(userId)
                    .orderId(orderId)
                    .build();
        }
    }
}
