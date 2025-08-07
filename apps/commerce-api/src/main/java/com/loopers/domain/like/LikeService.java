package com.loopers.domain.like;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean likeProduct(LikeCommand.Like command) {
        Like like = Like.create(command.getUserId(), command.getProductId());
        try {
            likeRepository.save(like);
            return true;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean unLikeProduct(LikeCommand.Unlike command) {
        Long affected = likeRepository.deleteByUserIdAndProductId(command.getUserId(), command.getProductId());
        return affected > 0;
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
