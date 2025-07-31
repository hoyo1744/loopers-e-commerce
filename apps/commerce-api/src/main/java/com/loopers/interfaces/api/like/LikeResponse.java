package com.loopers.interfaces.api.like;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class LikeResponse {

    @Getter
    @Builder
    public static class Products {

        List<Product> products;

        private Products(List<Product> products) {
            this.products = products;
        }

        public static Products of(List<Product> products) {
            return Products.builder()
                    .products(products)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class Product {
        private String name;
        private Long price;
        private Brand brand;
        private Like like;
        private Stock stock;


        private Product(String name, Long price, Brand brand, Like like, Stock stock) {
            this.name = name;
            this.price = price;
            this.brand = brand;
            this.like = like;
            this.stock = stock;
        }

        public static Product of(String name, Long price, Brand brand, Like like, Stock stock) {
            return Product.builder()
                    .name(name)
                    .price(price)
                    .brand(brand)
                    .like(like)
                    .stock(stock)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class Brand {
        private String name;

        private Brand(String name) {
            this.name = name;
        }

        public static Brand of(String name) {
            return Brand.builder()
                    .name(name)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class Like {
        private Boolean liked;
        private Long count;

        private Like(Boolean liked, Long count) {
            this.liked = liked;
            this.count = count;
        }

        public static Like of(Boolean liked, Long count) {
            return Like.builder()
                    .liked(liked)
                    .count(count)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class Stock {
        private Long quantity;

        private Stock(Long quantity) {
            this.quantity = quantity;
        }
        public static Stock of(Long quantity) {
            return Stock.builder()
                    .quantity(quantity)
                    .build();
        }
    }


}
