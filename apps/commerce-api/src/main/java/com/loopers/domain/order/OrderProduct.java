package com.loopers.domain.order;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "order_product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderProduct {

    @Id
    @Column(name = "order_product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    private Long productId;

    private Long price;

    private Long quantity;

    @Builder
    private OrderProduct(Order order, Long productId, Long price, Long quantity) {
        this.order = order;
        this.productId = productId;
        this.price = price;
        this.quantity = quantity;
    }

    public static OrderProduct create(Order order, Long productId, Long price, Long quantity) {

        return OrderProduct.builder()
                .order(order)
                .productId(productId)
                .price(price)
                .quantity(quantity)
                .build();
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Long calculatePrice() {
        return price * quantity;
    }
}
