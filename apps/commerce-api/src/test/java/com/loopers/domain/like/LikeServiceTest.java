package com.loopers.domain.like;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @InjectMocks
    private LikeService likeService;

    @DisplayName("좋아요 여부 확인 테스트")
    @Nested
    class IsLiked {

        @Test
        @DisplayName("사용자가 특정 상품을 좋아요했는지 확인한다.")
        void shouldReturnTrueIfLiked() {
            // given
            String userId = "user1";
            Long productId = 100L;
            when(likeRepository.existsByUserIdAndProductId(userId, productId)).thenReturn(true);

            // when
            boolean result = likeService.isLiked(LikeCommand.Check.of(userId, productId));

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("사용자가 특정 상품을 좋아요하지 않았다면 false를 반환한다.")
        void shouldReturnFalseIfNotLiked() {
            // given
            String userId = "user1";
            Long productId = 100L;
            when(likeRepository.existsByUserIdAndProductId(userId, productId)).thenReturn(false);

            // when
            boolean result = likeService.isLiked(LikeCommand.Check.of(userId, productId));

            // then
            assertThat(result).isFalse();
        }
    }

    @DisplayName("좋아요 개수 확인 테스트")
    @Nested
    class CountLikes {
        @Test
        @DisplayName("특정 상품의 좋아요 수를 반환한다.")
        void shouldReturnLikeCount() {
            // given
            Long productId = 100L;
            when(likeRepository.countByProductId(productId)).thenReturn(5L);

            // when
            Long result = likeService.countLikes(productId);

            // then
            assertThat(result).isEqualTo(5L);
        }
    }

    @DisplayName("좋아요 등록 테스트")
    @Nested
    class LikeProduct {

        @Test
        @DisplayName("좋아요 추가에 성공하면 true를 반환한다.")
        void shouldReturnTrueOnSuccess() {
            // given
            Like like = Like.create("user1", 100L);
            LikeCommand.Like command = LikeCommand.Like.of("user1", 100L);
            when(likeRepository.save(any(Like.class))).thenReturn(like);

            // when
            boolean result = likeService.likeProduct(command);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("좋아요가 중복되어 예외가 발생하면 false를 반환한다.")
        void shouldReturnFalseOnDuplicate() {
            // given
            LikeCommand.Like command = LikeCommand.Like.of("user1", 100L);
            when(likeRepository.save(any(Like.class)))
                    .thenThrow(new org.springframework.dao.DataIntegrityViolationException("중복"));

            // when
            boolean result = likeService.likeProduct(command);

            // then
            assertThat(result).isFalse();
        }
    }

    @DisplayName("좋아요 해제 테스트")
    @Nested
    class UnLikeProduct {

        @Test
        @DisplayName("좋아요 해제가 정상적으로 되면 true를 반환한다.")
        void shouldReturnTrueWhenDeleted() {
            // given
            String userId = "user123";
            Long productId = 1L;
            LikeCommand.Unlike command = LikeCommand.Unlike.of(userId, productId);

            when(likeRepository.deleteByUserIdAndProductId(userId, productId)).thenReturn(1L);

            // when
            boolean result = likeService.unLikeProduct(command);

            // then
            assertThat(result).isTrue();
            verify(likeRepository).deleteByUserIdAndProductId(userId, productId);
        }

        @Test
        @DisplayName("좋아요 해제 대상이 없으면 false를 반환한다.")
        void shouldReturnFalseWhenNotDeleted() {
            // given
            String userId = "user123";
            Long productId = 1L;
            LikeCommand.Unlike command = LikeCommand.Unlike.of(userId, productId);

            when(likeRepository.deleteByUserIdAndProductId(userId, productId)).thenReturn(0L);

            // when
            boolean result = likeService.unLikeProduct(command);

            // then
            assertThat(result).isFalse();
            verify(likeRepository).deleteByUserIdAndProductId(userId, productId);
        }
    }

}
