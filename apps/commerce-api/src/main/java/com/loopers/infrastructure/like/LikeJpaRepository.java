package com.loopers.infrastructure.like;

import com.loopers.domain.like.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeJpaRepository extends JpaRepository<Like, Long> {
    boolean existsByUserIdAndProductId(String userId, Long productId);

    long countByProductId(Long productId);

    Optional<Like> findByUserIdAndProductId(String userId, Long productId);

    Like save(Like like);

    Long deleteByUserIdAndProductId(String userId, Long productId);

    List<Like> findAllByUserId(String userId);

}
