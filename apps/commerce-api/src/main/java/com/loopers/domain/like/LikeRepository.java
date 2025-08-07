package com.loopers.domain.like;

import java.util.List;
import java.util.Optional;

public interface LikeRepository {
    boolean existsByUserIdAndProductId(String userId, Long productId);

    long countByProductId(Long productId);

    Optional<Like> findByUserIdAndProductId(String userId, Long productId);

    Like save(Like like);

    void delete(String userId, Long productId);

    List<Like> findAllByUserId(String userId);

    Long deleteByUserIdAndProductId(String userId, Long productId);
}
