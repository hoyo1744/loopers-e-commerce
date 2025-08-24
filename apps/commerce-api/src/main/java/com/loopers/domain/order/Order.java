package com.loopers.domain.order;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderNumber;

    private String userId;

    private Long userCouponId;

    private OrderStatus orderStatus;

    private Long totalPrice;

    private Long discountPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderProduct> orderProducts = new ArrayList<>();

    @Builder
    private Order(String userId, String orderNumber, OrderStatus orderStatus, Long totalPrice, List<OrderProduct> orderProducts) {
        this.userId = userId;
        this.orderNumber = orderNumber;
        this.orderStatus = orderStatus;
        this.totalPrice = totalPrice;
        this.orderProducts = orderProducts;
    }

    public void updateOrderStatus(OrderStatus orderStatus) {
        validationOrderStatus(orderStatus);
        this.orderStatus = orderStatus;
    }

    private static void validationOrderStatus(OrderStatus orderStatus) {
        if (orderStatus == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "주문 상태는 비어있을 수 없습니다.");
        }
    }

    public static Order create(String userId, OrderCommand.OrderProducts command) {

        Order order = Order.builder()
                .userId(userId)
                .orderNumber(UUID.randomUUID().toString())
                .orderStatus(OrderStatus.PENDING)
                .totalPrice(0L) // 임시 값
                .orderProducts(new ArrayList<>())
                .build();

        List<OrderProduct> orderProducts = command.getOrderProducts().stream().map(op -> OrderProduct.create(
                order,
                op.getProductId(),
                op.getPrice(),
                op.getQuantity())).collect(Collectors.toList());

        long totalPrice = orderProducts.stream().mapToLong(OrderProduct::calculatePrice).sum();

        order.totalPrice = totalPrice;
        order.orderProducts.addAll(orderProducts);

        return order;
    }

    public void applyDiscount(Long userCouponId, Long discountAmount) {
        if (discountAmount < 0) {
            throw new IllegalArgumentException("할인 금액은 0 이상이어야 합니다.");
        }
        this.userCouponId = userCouponId;
        this.discountPrice = discountAmount;
    }

    public Long calculateFinalPrice() {
        long discount = (discountPrice != null) ? discountPrice : 0L;
        return totalPrice - discount;
    }

}
