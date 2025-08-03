package com.loopers.interfaces.api.product;

import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

public class ProductResponse {

    @Getter
    public static class Products {
        private List<Product> products;

        private Products(List<Product> products) {
            this.products = products;
        }

        public static Products of (List<Product> products) {
            return new Products(products);
        }
    }


    @Getter
    @Builder
    public static class Product {
        private String name;
        private Long price;
        private ZonedDateTime createdAt;
        private Brand brand;
        private Like like;

        private Product(String name, Long price, ZonedDateTime createdAt, Brand brand, Like like) {
            this.name = name;
            this.price = price;
            this.createdAt = createdAt;
            this.brand = brand;
            this.like = like;
        }

        public static Product of(String name, Long price, ZonedDateTime createdAt, Brand brand, Like like) {
            return Product.builder()
                    .name(name)
                    .price(price)
                    .createdAt(createdAt)
                    .brand(brand)
                    .like(like)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class ProductDetail {

        private String name;
        private Long price;

        private Brand brand;

        private Like like;

        private Stock stock;

        private ProductDetail(String name, Long price, Brand brand, Like like, Stock stock) {
            this.name = name;
            this.price = price;
            this.brand = brand;
            this.like = like;
            this.stock = stock;
        }


        public static ProductDetail of(String name, Long price, Brand brand, Like like, Stock stock) {
            return ProductDetail.builder()
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
            return Brand.builder().name(name).build();
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
            return Stock.builder().quantity(quantity).build();
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
            return Like.builder().liked(liked).count(count).build();
        }
    }
}
