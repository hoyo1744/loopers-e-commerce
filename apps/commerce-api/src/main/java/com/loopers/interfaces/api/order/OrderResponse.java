package com.loopers.interfaces.api.order;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class OrderResponse {

    @Getter
    @Builder
    public static class Orders {
        List<Order> orders;

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
        private String orderStatus;
        private Long totalPrice;

        private List<Product> products;

        private Order(String orderStatus, Long totalPrice, List<Product> products) {
            this.orderStatus = orderStatus;
            this.totalPrice = totalPrice;
            this.products = products;
        }

        public static Order of(String orderStatus, Long totalPrice, List<Product> products) {
            return Order.builder()
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

        public static Product of(String name, Long price, String brand) {
            return Product.builder()
                    .name(name)
                    .price(price)
                    .brand(brand)
                    .build();
        }
    }
}
