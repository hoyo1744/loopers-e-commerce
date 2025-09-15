package com.loopers.domain.like;

import com.loopers.domain.trace.TraceEventPublisher;
import com.loopers.domain.trace.TraceLikeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;

    private final LikeEventPublisher likeEventPublisher;

    private final TraceEventPublisher traceEventPublisher;

    public boolean isLiked(LikeCommand.Check command) {
        return likeRepository.existsByUserIdAndProductId(command.getUserId(), command.getProductId());
    }

    public Set<Long> getLikedProductIds(LikeCommand.LikeProducts command) {
        return likeRepository.findLikedProductIds(command.getUserId(), command.getProductIds());
    }


    public Long countLikes(Long productId) {
        return likeRepository.countByProductId(productId);
    }

    @Transactional
    public void likeProduct(LikeCommand.Like command) {
        Like like = Like.create(command.getUserId(), command.getProductId());
        likeRepository.insertIfNotExists(like.getUserId(), like.getProductId());
        likeEventPublisher.publish(LikeEvent.Like.of(like.getProductId(), like.getUserId()));
        traceEventPublisher.publish(TraceLikeEvent.LikeCreated.of(like.getUserId(), like.getProductId()));
    }

    @Transactional
    public void unLikeProduct(LikeCommand.Unlike command) {
        likeRepository.deleteByUserIdAndProductId(command.getUserId(), command.getProductId());
        likeEventPublisher.publish(LikeEvent.Unlike.of(command.getProductId(), command.getUserId()));
        traceEventPublisher.publish(TraceLikeEvent.LikeCanceled.of(command.getUserId(), command.getProductId()));
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

    public List<Long> getLikedProductIdsByUserId(String userId) {
        return likeRepository.findLikedProductIdsByUserId(userId);
    }
}
