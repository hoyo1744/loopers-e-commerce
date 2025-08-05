package com.loopers.application.order;

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

        private Order(String userId, List<OrderProduct> orderProducts, Long couponId) {
            this.userId = userId;
            this.orderProducts = orderProducts;
            this.couponId = couponId;
        }

        public static Order of(String userId, List<OrderProduct> orderProducts, Long couponId) {
            return Order.builder()
                    .userId(userId)
                    .orderProducts(orderProducts)
                    .couponId(couponId)
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
