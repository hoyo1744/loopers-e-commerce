package com.loopers.domain.like;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;

    public boolean isLiked(LikeCommand.Check command) {
        return likeRepository.existsByUserIdAndProductId(command.getUserId(), command.getProductId());
    }

    public Set<Long> getLikedProductIds(LikeCommand.LikeProducts command) {
        return likeRepository.findLikedProductIds(command.getUserId(), command.getProductIds());
    }


    public Long countLikes(Long productId) {
        return likeRepository.countByProductId(productId);
    }

    public void likeProduct(LikeCommand.Like command) {
        Like like = Like.create(command.getUserId(), command.getProductId());
        likeRepository.insertIfNotExists(like.getUserId(), like.getProductId());
    }

    public void unLikeProduct(LikeCommand.Unlike command) {
        likeRepository.deleteByUserIdAndProductId(command.getUserId(), command.getProductId());
    }

    public List<LikeInfo.LikeProduct> getLikeProduct(String userId) {
        return likeRepository.findAllByUserId(userId)
                .stream()
                .map(like -> LikeInfo.LikeProduct.of(
                                like.getUserId(),
                                like.getProductId()
                        )
                ).toList();
    }
}
