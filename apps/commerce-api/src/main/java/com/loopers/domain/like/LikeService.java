package com.loopers.domain.like;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;

    public boolean isLiked(LikeCommand.Check command) {
        return likeRepository.existsByUserIdAndProductId(command.getUserId(), command.getProductId());
    }

    public Long countLikes(Long productId) {
        return likeRepository.countByProductId(productId);
    }

    public boolean likeProduct(LikeCommand.Like command) {
        if (likeRepository.existsByUserIdAndProductId(command.getUserId(), command.getProductId())) {
            return false;
        }
        Like like = Like.create(command.getUserId(), command.getProductId());
        likeRepository.save(like);
        return true;
    }

    public boolean unLikeProduct(LikeCommand.Unlike command) {
        if (!likeRepository.existsByUserIdAndProductId(command.getUserId(), command.getProductId())) {
            return false;
        }
        likeRepository.delete(command.getUserId(), command.getProductId());
        return true;
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
