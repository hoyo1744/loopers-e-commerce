package com.loopers.application.order;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class OrderResult {

    @Getter
    @Builder
    public static class Orders {
        private List<Order> orders;

        private Orders(List<Order> orders) {
            this.orders = orders;
        }

        public static Orders of(List<Order> orders) {
            return Orders.builder()
                    .orders(orders)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class Order {
        private Long orderId;
        private String orderStatus;
        private Long totalPrice;
        private List<Product> products;

        private Order(Long orderId, String orderStatus, Long totalPrice, List<Product> products) {
            this.orderId = orderId;
            this.orderStatus = orderStatus;
            this.totalPrice = totalPrice;
            this.products = products;
        }

        public static Order of(Long orderId, String orderStatus, Long totalPrice, List<Product> products) {
            return Order.builder()
                    .orderId(orderId)
                    .orderStatus(orderStatus)
                    .totalPrice(totalPrice)
                    .products(products)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class Product {
        private String name;
        private Long price;
        private String brand;

        private Product(String name, Long price, String brand) {
            this.name = name;
            this.price = price;
            this.brand = brand;
        }

        public static OrderResult.Product of(String name, Long price, String brand) {
            return OrderResult.Product.builder()
                    .name(name)
                    .price(price)
                    .brand(brand)
                    .build();
        }
    }
}
