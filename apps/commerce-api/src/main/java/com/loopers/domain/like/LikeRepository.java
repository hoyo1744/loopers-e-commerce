package com.loopers.domain.like;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface LikeRepository {
    boolean existsByUserIdAndProductId(String userId, Long productId);

    long countByProductId(Long productId);

    Like save(Like like);

    List<Like> findAllByUserId(String userId);

    Long deleteByUserIdAndProductId(String userId, Long productId);

    Integer insertIfNotExists(String userId, Long productId);

    Set<Long> findLikedProductIds(String userId, List<Long> productIds);

}
