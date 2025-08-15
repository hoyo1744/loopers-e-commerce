package com.loopers.domain.product;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "product",
        indexes = {
                @Index(name = "idx_product_created_at_id", columnList = "created_at, id"),
                @Index(name = "idx_product_price_id", columnList = "price, id"),
                @Index(name = "idx_product_like_count_id", columnList = "like_count, id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(name = "brand_id")
    private Long brandId;

    private String name;

    private Long price;

    private Long likeCount;

    @Builder
    private Product(Long brandId, String name, Long price, Long likeCount) {
        this.brandId = brandId;
        this.name = name;
        this.price = price;
        this.likeCount = likeCount;
    }

    public static Product create(Long brandId, String name, Long price) {
        validationBrand(brandId);
        validationName(name);
        validationPrice(price);

        return Product.builder()
                .brandId(brandId)
                .name(name)
                .price(price)
                .likeCount(0L)
                .build();
    }

    public static void validationBrand(Long brandId) {
        if (brandId == null || brandId <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "존재하지 않는 브랜드 ID 입니다.");
        }
    }

    public static void validationName(String name) {
        if (name == null || name.isBlank()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "상품 이름은 비어있을 수 없습니다.");
        }
    }

    public static void validationPrice(Long price) {
        if (price == null || price < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "상품 가격은 0보다 작을 수 없습니다.");
        }
    }
}
