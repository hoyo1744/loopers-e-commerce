package com.loopers.infrastructure.like;

import com.loopers.domain.like.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface LikeJpaRepository extends JpaRepository<Like, Long> {
    boolean existsByUserIdAndProductId(String userId, Long productId);

    long countByProductId(Long productId);

    Like save(Like like);

    Long deleteByUserIdAndProductId(String userId, Long productId);

    List<Like> findAllByUserId(String userId);

    @Modifying
    @Query(value = """
    INSERT IGNORE INTO likes(user_id, product_id)
    VALUES (:userId, :productId)
    """, nativeQuery = true)
    int insertIfNotExists(@Param("userId") String userId, @Param("productId") Long productId);

    @Query("""
    SELECT l.productId
    FROM Like l
    WHERE l.userId = :userId
      AND l.productId IN :productIds
     """)
    Set<Long> findLikedProductIds(@Param("userId") String userId,
                                  @Param("productIds") List<Long> productIds);
}
