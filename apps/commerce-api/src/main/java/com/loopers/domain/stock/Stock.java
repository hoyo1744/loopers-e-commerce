package com.loopers.domain.stock;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "stack")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {

    @Id
    @Column(name = "stock_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    private Long quantity;

    @Builder
    private Stock(Long id, Long productId, Long quantity) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
    }

    public static Stock create(Long productId, Long quantity) {

        validationProduct(productId);
        validationQuantity(quantity);

        return Stock.builder()
                .productId(productId)
                .quantity(quantity)
                .build();
    }

    public static void validationProduct(Long productId) {
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("존재하지 않는 상품 ID 입니다.");
        }
    }

    public static void validationQuantity(Long quantity) {
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("재고 수량은 0 이상이어야 합니다.");
        }
    }

    public void incrementQuantity(Long quantity) {
        this.quantity += quantity;
    }

    public void decrementQuantity(Long quantity) {
        this.quantity -= quantity;
    }

    public void hasEnough(Long requestedQuantity) {
        if (requestedQuantity == null || requestedQuantity <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "요청 수량은 1 이상이어야 합니다.");
        }

        if (this.quantity < requestedQuantity) {
            throw new CoreException(ErrorType.BAD_REQUEST, "재고가 부족합니다.");
        }
    }
}
