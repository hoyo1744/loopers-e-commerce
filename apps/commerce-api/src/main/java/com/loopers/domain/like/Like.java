package com.loopers.domain.like;


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
        name = "likes",
        uniqueConstraints = @UniqueConstraint(name = "uk_likes_user_product", columnNames = {"user_id", "product_id"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Like {

    @Id
    @Column(name = "like_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private Long productId;

    @Builder
    private Like(Long id, Long productId, String userId) {
        this.id = id;
        this.productId = productId;
        this.userId = userId;
    }

    public static Like create(String userId, Long productId) {

        validationUser(userId);
        validationProduct(productId);

        return Like.builder()
                .productId(productId)
                .userId(userId)
                .build();
    }

    public static void validationUser(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new CoreException(ErrorType.UNAUTHORIZED, "인증되지 않은 사용자 ID 입니다.");
        }
    }

    public static void validationProduct(Long productId) {
        if (productId == null || productId <= 0) {
            throw new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 상품 ID 입니다.");
        }
    }
}
