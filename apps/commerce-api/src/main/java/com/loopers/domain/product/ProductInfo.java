package com.loopers.domain.product;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

public class ProductInfo {

    @Getter
    @Builder
    public static class ProductQuery {
        private Long productId;
        private String productName;
        private Long price;
        private ZonedDateTime createdAt;
        private Long brandId;
        private String brandName;
        private Boolean isLiked;
        private Long likes;

        public ProductQuery(Long productId, String productName, Long price, ZonedDateTime createdAt, Long brandId, String brandName, Boolean isLiked, Long likes) {
            this.productId = productId;
            this.productName = productName;
            this.price = price;
            this.createdAt = createdAt;
            this.brandId = brandId;
            this.brandName = brandName;
            this.isLiked = isLiked;
            this.likes = likes;
        }

        public static ProductQuery of(Long productId, String productName, Long price, ZonedDateTime createdAt, Long brandId, String brandName, Boolean isLiked, Long likes, Long quantity) {
            return ProductQuery.builder()
                    .productId(productId)
                    .productName(productName)
                    .price(price)
                    .createdAt(createdAt)
                    .brandId(brandId)
                    .brandName(brandName)
                    .isLiked(isLiked)
                    .likes(likes)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class Product{
        private Long id;
        private String name;
        private Long price;
        private Long brandId;

        private Product(Long id, String name, Long price, Long brandId) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.brandId = brandId;
        }

        public static Product of(Long id, String name, Long price, Long brandId) {
            return Product.builder()
                    .id(id)
                    .name(name)
                    .price(price)
                    .brandId(brandId)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class ProductDetail {
        private String name;
        private Long price;
        private Long brandId;

        private ProductDetail(String name, Long price, Long brandId) {
            this.name = name;
            this.price = price;
            this.brandId = brandId;
        }

        public static ProductDetail of(String productName, Long price, Long brandId) {
            return new ProductDetail(productName, price, brandId);
        }
    }

    @Getter
    @Builder
    public static class OrderProducts {
        private List<OrderProduct> orderProducts;

        private OrderProducts(List<OrderProduct> orderProducts) {
            this.orderProducts = orderProducts;
        }

        public static OrderProducts of(List<OrderProduct> orderProducts) {
            return OrderProducts.builder()
                    .orderProducts(orderProducts)
                    .build();
        }
    }


    @Getter
    @Builder
    public static class OrderProduct {
        private Long productId;
        private Long price;
        private Long quantity;
        private String productName;

        private OrderProduct(Long productId, Long price, Long quantity, String productName) {
            this.productId = productId;
            this.price = price;
            this.quantity = quantity;
            this.productName = productName;
        }

        public static OrderProduct of(Long productId, Long price, Long quantity, String productName) {
            return OrderProduct.builder().
                    productId(productId)
                    .price(price)
                    .quantity(quantity)
                    .productName(productName)
                    .build();
        }
    }
}
